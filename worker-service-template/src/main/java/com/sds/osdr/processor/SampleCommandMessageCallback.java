package com.sds.osdr.processor;

import java.util.concurrent.BlockingQueue;

import com.sds.osdr.model.SampleCommand;

public class SampleCommandMessageCallback extends AbstractMessageCallback<SampleCommand> {

    public SampleCommandMessageCallback(Class<SampleCommand> tClass, BlockingQueue<SampleCommand> queue) {
        super(tClass, queue);
    }

}
