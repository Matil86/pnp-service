#!/usr/bin/env python3
"""
PR Risk Classifier
Analyzes PR changes and assigns risk level: express_lane, standard, or high_risk
"""

import os
import sys
import yaml
import re
from pathlib import Path
from typing import List, Dict, Tuple


def load_config() -> Dict:
    """Load risk classification configuration."""
    config_path = Path(".github/PR_RISK_CLASSIFIER.yml")
    with open(config_path) as f:
        return yaml.safe_load(f)


def get_changed_files() -> List[str]:
    """Get list of changed files in the PR."""
    # Read from git diff
    import subprocess
    result = subprocess.run(
        ["git", "diff", "--name-only", "origin/main...HEAD"],
        capture_output=True,
        text=True
    )
    return result.stdout.strip().split("\n")


def get_lines_changed() -> int:
    """Get total lines changed in the PR."""
    import subprocess
    result = subprocess.run(
        ["git", "diff", "--stat", "origin/main...HEAD"],
        capture_output=True,
        text=True
    )
    # Parse output like "5 files changed, 123 insertions(+), 45 deletions(-)"
    output = result.stdout.strip().split("\n")[-1]

    insertions = 0
    deletions = 0

    if "insertion" in output:
        insertions = int(re.search(r"(\d+) insertion", output).group(1))
    if "deletion" in output:
        deletions = int(re.search(r"(\d+) deletion", output).group(1))

    return insertions + deletions


def matches_pattern(file_path: str, patterns: List[str]) -> bool:
    """Check if file matches any glob pattern."""
    from fnmatch import fnmatch
    return any(fnmatch(file_path, pattern) for pattern in patterns)


def contains_keyword(file_path: str, keywords: List[str]) -> bool:
    """Check if file contains any keyword."""
    try:
        with open(file_path) as f:
            content = f.read().lower()
            return any(keyword.lower() in content for keyword in keywords)
    except:
        return False


def classify_risk(config: Dict, files: List[str], lines_changed: int) -> Tuple[str, str]:
    """
    Classify PR risk level.
    Returns: (risk_level, reason)
    """

    # Check HIGH RISK first (most important)
    high_risk_config = config["high_risk"]

    # Check high-risk paths
    for file in files:
        if matches_pattern(file, high_risk_config["paths"]):
            return ("high_risk", f"High-risk path: {file}")

    # Check high-risk keywords in changed files
    for file in files:
        if file.endswith(".kt") or file.endswith(".java"):
            if contains_keyword(file, high_risk_config["keywords"]):
                return ("high_risk", f"High-risk keyword detected in {file}")

    # Check high-risk patterns
    for file in files:
        for pattern in high_risk_config["patterns"]:
            if re.search(pattern, file):
                return ("high_risk", f"High-risk pattern match: {file}")

    # Check EXPRESS LANE (documentation, config only)
    express_config = config["express_lane"]

    if lines_changed <= express_config["size_limit"]:
        # All files must match express lane patterns
        if all(matches_pattern(f, express_config["paths"]) for f in files):
            return ("express_lane", f"Documentation/config only ({lines_changed} lines)")

    # Check STANDARD (exclude high-risk paths)
    standard_config = config["standard"]

    if lines_changed <= standard_config.get("size_limit", 500):
        # Check if any files are in excluded paths
        excluded = standard_config.get("exclude", [])
        if not any(matches_pattern(f, excluded) for f in files):
            # Check if files are in standard paths
            if any(matches_pattern(f, standard_config["paths"]) for f in files):
                return ("standard", f"Standard development ({lines_changed} lines)")

    # Default to HIGH RISK if uncertain
    return ("high_risk", f"Large change or unclear classification ({lines_changed} lines)")


def main():
    """Main classification logic."""
    config = load_config()
    files = get_changed_files()
    lines_changed = get_lines_changed()

    if not files or files == ['']:
        print("No files changed")
        sys.exit(0)

    risk_level, reason = classify_risk(config, files, lines_changed)

    print(f"Risk Level: {risk_level}")
    print(f"Reason: {reason}")
    print(f"Files Changed: {len(files)}")
    print(f"Lines Changed: {lines_changed}")

    # Set GitHub output
    if "GITHUB_OUTPUT" in os.environ:
        with open(os.environ["GITHUB_OUTPUT"], "a") as f:
            f.write(f"risk_level={risk_level}\n")
            f.write(f"reason={reason}\n")

    # Set labels for GitHub Actions
    print(f"\n::set-output name=risk_level::{risk_level}")
    print(f"::set-output name=label::risk:{risk_level}")


if __name__ == "__main__":
    main()
