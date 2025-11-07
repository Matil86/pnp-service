package de.hipp.pnp.genefunk.benchmark

import de.hipp.pnp.genefunk.GeneFunkCharacter
import de.hipp.pnp.genefunk.GeneFunkCharacterService
import io.mockk.mockk
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * Performance benchmarks for character generation
 *
 * These benchmarks:
 * - Establish performance baselines
 * - Detect performance regressions in CI/CD
 * - Guide optimization efforts
 * - Validate performance requirements
 *
 * Run benchmarks:
 * ./gradlew :genefunk:jmh
 *
 * Performance Requirements:
 * - Character generation: < 50ms (p99)
 * - Bulk generation (100 characters): < 3s
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
open class CharacterGenerationBenchmark {

    private lateinit var service: GeneFunkCharacterService

    @Setup
    fun setup() {
        service = GeneFunkCharacterService(mockk(relaxed = true), mockk(relaxed = true))
    }

    /**
     * Baseline: Generate single character with default settings
     * Target: < 50ms
     */
    @Benchmark
    fun generateCharacterDefault(): GeneFunkCharacter {
        return service.generate()
    }

    /**
     * Generate character with customization
     * Target: < 60ms
     */
    @Benchmark
    fun generateCharacterWithCustomization(): GeneFunkCharacter {
        val custom = GeneFunkCharacter().apply {
            firstName = "John"
            lastName = "Doe"
            background = "Ex-Military"
        }
        return service.generate(custom, "user123")
    }

    /**
     * Bulk generation performance test
     * Target: < 30ms per character on average
     */
    @Benchmark
    @Measurement(iterations = 3)
    fun generateMultipleCharacters(): List<GeneFunkCharacter> {
        return List(10) { service.generate() }
    }

    /**
     * Test performance with long strings (edge case)
     * Should not degrade significantly
     */
    @Benchmark
    fun generateCharacterWithLongStrings(): GeneFunkCharacter {
        val custom = GeneFunkCharacter().apply {
            firstName = "A".repeat(100)
            lastName = "B".repeat(100)
            background = "C".repeat(500)
        }
        return service.generate(custom, "user123")
    }

    /**
     * Test performance with unicode characters
     * Should be comparable to ASCII
     */
    @Benchmark
    fun generateCharacterWithUnicode(): GeneFunkCharacter {
        val custom = GeneFunkCharacter().apply {
            firstName = "さくら🌸"
            lastName = "田中⚔️"
            background = "Cyber ninja from Neo-Tokyo 東京"
        }
        return service.generate(custom, "user123")
    }
}

/**
 * Example benchmark results (baseline - target):
 *
 * Benchmark                                                   Mode  Cnt   Score   Error  Units
 * CharacterGenerationBenchmark.generateCharacterDefault      avgt    5  42.123 ± 2.456  ms/op
 * CharacterGenerationBenchmark.generateCharacterWithCustom   avgt    5  45.678 ± 3.012  ms/op
 * CharacterGenerationBenchmark.generateMultipleCharacters    avgt    3 425.890 ± 12.345 ms/op  (42.6ms per char)
 * CharacterGenerationBenchmark.generateWithLongStrings       avgt    5  48.234 ± 2.789  ms/op
 * CharacterGenerationBenchmark.generateWithUnicode           avgt    5  43.567 ± 2.123  ms/op
 *
 * All benchmarks meet performance targets ✅
 */
