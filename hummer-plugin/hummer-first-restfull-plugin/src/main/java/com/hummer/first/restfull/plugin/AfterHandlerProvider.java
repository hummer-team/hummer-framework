package com.hummer.first.restfull.plugin;

/**
 * @author lee
 */
public interface AfterHandlerProvider<T> {
    /**
     * business handler e.g: verify
     *
     * @param data api response
     */
    void handler(T data);
}
