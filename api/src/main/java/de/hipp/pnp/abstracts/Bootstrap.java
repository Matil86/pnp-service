package de.hipp.pnp.abstracts;

public abstract class Bootstrap {

    protected Bootstrap(){
        initialize();
    }

    protected abstract void initialize();
}
