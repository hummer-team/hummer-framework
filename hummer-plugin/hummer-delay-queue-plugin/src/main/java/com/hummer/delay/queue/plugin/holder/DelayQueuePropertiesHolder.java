package com.hummer.delay.queue.plugin.holder;

import com.hummer.core.PropertiesContainer;

/**
 * DelayQueuePropertiesHolder
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/8 19:02
 */
public class DelayQueuePropertiesHolder {

    public static boolean getDelayQueueSchedulePollEnable() {

        return PropertiesContainer.get("delay.queue.poll.schedule.enable", Boolean.class, true);
    }

    public static int getDelayQueuePollPeriodMillions() {

        return PropertiesContainer.valueOfInteger("delay.queue.poll.schedule.period.millions", 3000);
    }

    public static int getDelayQueuePollInitialDelayMillions() {

        return PropertiesContainer.valueOfInteger("delay.queue.poll.schedule.initial.delay.millions", 3000);
    }
}
