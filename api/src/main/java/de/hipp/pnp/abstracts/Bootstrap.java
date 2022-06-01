package de.hipp.pnp.abstracts;

public abstract class Bootstrap {

    public Bootstrap(){
        initialize();
    }

    protected abstract void initialize();
}
