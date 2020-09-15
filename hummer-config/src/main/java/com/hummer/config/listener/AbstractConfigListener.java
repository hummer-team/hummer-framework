package com.hummer.config.listener;

/**
 * AbstractConfigListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/1 10:04
 */
public abstract class AbstractConfigListener implements ConfigListener {

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean equals(ConfigListener listener) {
        if (listener == null) {
            return false;
        }
        return this.getClass() == listener.getClass()
                && listener.getId() != null
                && this.getId().equals(listener.getId());
    }
}
