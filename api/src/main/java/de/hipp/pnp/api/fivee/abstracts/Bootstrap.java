package de.hipp.pnp.api.fivee.abstracts;

public abstract class Bootstrap {

  protected Bootstrap() {
    initialize();
  }

  protected abstract void initialize();
}
