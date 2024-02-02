package com.example.entity;

public interface ThreadPoolParameter {

    /**
     * Get tenant id
     *
     * @return
     */
    String getTenantId();

    /**
     * Get item id
     *
     * @return
     */
    String getItemId();

    /**
     * Get thread-pool id
     *
     * @return
     */
    String getTpId();

    /**
     * Get core size
     *
     * @return
     */
    Integer getCoreSize();

    /**
     * Get max size
     *
     * @return
     */
    Integer getMaxSize();

    /**
     * Get queue type
     *
     * @return
     */
    Integer getQueueType();

    /**
     * Get capacity
     *
     * @return
     */
    Integer getCapacity();

    /**
     * Get keep alive time
     *
     * @return
     */
    Long getKeepAliveTime();

    /**
     * Get execute time out
     *
     * @return
     */
    Long getExecuteTimeOut();

    /**
     * Get rejected type
     *
     * @return
     */
    Integer getRejectedType();

    /**
     * Get is alarm
     *
     * @return
     */
    Integer getIsAlarm();

    /**
     * Get capacity alarm
     *
     * @return
     */
    Integer getCapacityAlarm();

    /**
     * Get liveness alarm
     *
     * @return
     */
    Integer getLivenessAlarm();

    /**
     * Get allow core thread timeOut
     *
     * @return
     */
    Integer getAllowCoreThreadTimeOut();
}
