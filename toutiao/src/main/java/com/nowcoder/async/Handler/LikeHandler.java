package com.nowcoder.async.Handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import org.apache.commons.collections.bag.SynchronizedSortedBag;

import java.util.Arrays;
import java.util.List;

/**
 * @author cyx
 * @data 2019/2/16 15:07
 */
public class LikeHandler implements EventHandler {


    @Override
    public void doHandle(EventModel model) {
        System.out.println("Liked");
    }

    @Override
    public List<EventType> getSupportEventType() {
        return Arrays.asList(EventType.Like);
    }
}
