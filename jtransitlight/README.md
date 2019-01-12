# JTransitLight


## What is JTransitLight

JTransitLight is a java library making it easy to communicate with other services that use MassTransit from C#.
MassTransit is a middleware solution implementing a service bus on top of a messaging system like RabbitMQ.
MassTransit is not available for java.
JTransitLight is only for environments using RabbitMQ as transport.

In JTransitLight's initial version, only publishing is supported.

## Installing

Currently you need to build JTransitLight yourself. No prebuilt version is available for the public.

## Connecting to the service bus

The service bus connection will be an object implementing interface IBusControl.
You make a connection to the service bus by using a factory:

```
    import com.npspot.jtransitlight.publisher.Bus;
    import com.npspot.jtransitlight.publisher.IBusControl;
    import com.npspot.jtransitlight.transport.JTransitLightTransportException;
    ....
    try 
    { 
        IBusControl bus = Bus.Factory.createUsingRabbitMq(...);
    }
    catch {JTransitLightTransportException e)
    {
        ....
    }
```

The alternative methods in the factory for creating RabbitMQ connections allows you to specify connection by host, username and password or by uri with optional username and password.
In addition you choose whether you want to send heartbeats (by interval), and if you want you must also provide a ThreadFactory.
See section on heartbeats below.

And one alternative method that can be used in test cases with no need for posting messages to RabbitMQ:

* IBusControl createUsingInMemory()

## Specifying the contract (message)

The contract is specified by creating a Java class with the required attributes.

In MassTransit, the Exchange name used when posting a message to RabbitMQ is created by combining the namespace of the contract with the contract class name.
In JTransitLight, the class name is still used, but the namespace isn't available, so we need to add this to the contract class. 
The requirement is that the namspace name can be accessed by a getter method: String getNamespace()

JTransitLight is using Jackson databinding to serialize the contract (to JSON format) before posting it on the queue.
To maintain control over which attributes that will be in the message, and how they are serialized, the contarct class has to have annotations.

Especially the extra annotation for Calendar type objects are important:
* For objects with date and time and C# type DateTime, use format without timezone: "yyyy-MM-dd'T'HH:mm:ss.SSS" 
* For objects with date only and C# type DateTime, use format without timezone: "yyyy-MM-dd"
* For objects with date and time and C# type DateTimeOffset, use format with timezone: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
* For objects with date only and C# type DateTimeOffset, use format with timezone: "yyyy-MM-ddXXX"

Without annotations, Jackson will use the getter methods to decide the name of the attributes and which attributes to serialize.
In my opinion, this is a bit messy. I suggest using explicit annotations on all attributes to include, and @JsonIgnore on everything else.

Example (note the extra annotation on the Calendar attribute).

The contract in C# (everybody is of course using DateTimeOffset, not DateTime):

```
namespace NPS.Contracts.FooContext
{
    public class FooInfo
    {
        public string Name { get; set; }
        public DateTimeOffset DateOfBirth { get; set; }
    }
}
```

The correcponding contract in Java:

```
public class FooInfo
{
    @JsonIgnore
    final private static String namespace = "NPS.Contracts.FooContext";

    @JsonProperty
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "CET")
    @JsonProperty
    private Calendar dateOfBirth;

    @JsonIgnore
    public void setName(String name){this.name = name;}


    @JsonIgnore
    public String getName(){return name;}

    @JsonIgnore
    public Calendar getDateOfBirth(){return dateOfBirth;}

    @JsonIgnore
    public void setDateOfBirth(Calendar dateOfBirth){this .dateOfBirth = dateOfBirth;}

    @JsonIgnore
    public static String getNamespace(){return namespace;}
}
```

Two properties will be seriallized from this contract, "name" and "dateOfBirth".
The JSON string published on the bus use camelcase (the same as noraml Java naming conventions), and the C# code receiving the message will use "Name" and "DateOfBirth".

The exchange this message is published to will be named: NPS.Contracys.FooContext:FooInfo

## Publish the message on the bus

The publishing is done using a method from the IBusControl returned when the bus connection was made:

```
import com.npspot.jtransitlight.JTransitLightException;
....
FooInfo contract = new FooInfo();
contract.setName("Ola Normann");
Calendar dateOfBirth = GregorianCalendar.getInstance();
dateOfBirth.set(1960, Calendar.AUGUST, 7, 4, 13, 36);
dateOfBirth.set(Calendar.MILLISECOND  , 0);
contract.setDateOfBirth(dateOfBirth);

try
{
    bus.publish(contract);
}
catch(JTransitLightException e)
{
    ....
}
```

## Discarding the bus to close connections

The bus connection should be discarded if it is not going to be used for a while.
Note that discarding the bus renders the IBusControl useless, so the IBusControl reference should also be discarded.
Using the bus again requires getting a new IBusControl from Bus.Factory....

```
try 
{
     bus.publish(contract);
    bus = null;
 } 
catch(JTransitLightException e) 
{
     .... 
}
```

## Heartbeats
Heartbeat is used not only for detecting connection state, but also for providing the last sequence number that was sent.
This is a way for the receiver to detect if they have lost any messages on the way.
This is not using the heartbeat feature of RabbitMQ (https://www.rabbitmq.com/heartbeats.html)

If you don't care about order of messages and send small amounts then you could skip heartbeat.

## Receiving Messages

```
package com.npspot.jtransitlight.example;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.consumer.Receiver;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

class ReceiverExample {
    
    private void receiveExampleMessages() {
        
        try {
            //First we get an instance of ReceiverBusControl using the Receiver Factory
            int handshakeTimeoutMs = 5000;
            ReceiverBusControl receiverBus = Receiver.Factory.createUsingRabbitMq(new URI("amqp://user:pass@xx.xx.xx.xx"), handshakeTimeoutMs);
            
            //Next we determine the exchange we want to connect to
            //In MassTransit in C#, the exchange name is determined by the namespace and the class name
            //In JTransitLight the exchange name is freely definable and if the publisher is using JTransitLight
            //They will have implemented the Contract interface, which defines two methods getNamespace() and getContractName()
            /**
             *   // Contract
             *
             * @JsonIgnore final private static String namespace =
             * "NPS.Contracts.Intraday";
             * @JsonIgnore final private static String contractName =
             * "TradeEvent";
             */
            //The exchange name is the namespace + ":" + contractName
            //So in the above example it would be NPS.Contracts.Intraday:TradeEvent
            ReceiverCallbackExample exampleCallback = new ReceiverCallbackExample();
            
            //We subscribe to the exchange, which in practice means binding the queue to the list of the exchanges provided
            //It is possible to bind a queue to multiple exchanges.
            //The queue name is arbitrary 
            //NOTE that each call to subscribe creates a new DefaultConsumer in RabbitMQ and calls exchangeDeclare and queueBind
            receiverBus.subscribe(Arrays.asList("NPS.Contracts.Intraday:TradeEvent"),"receiverQueueName",exampleCallback);
            
            //That is all, now exampleCallback.messageReceived is called whenever a message is received.
            
            
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(ReceiverExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JTransitLightTransportException ex) {
            Logger.getLogger(ReceiverExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JTransitLightException ex) {
            Logger.getLogger(ReceiverExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private class ReceiverCallbackExample implements ReceiverCallback {

        @Override
        public void messageReceived(byte[] message) {
            //Do something with the message
        }
        
    }
    
}
```

## Other properties 

JTransitLight is using durable exchanges in RabbitMQ which I believe is the correct choice.
This behaviour can be changed by useing a setter method on the IServiceBus:

`
bus.setUseDurableExchanges(boolean useDurableExchanges);
`

When posting messages on the bus, JTransitLight waits for acknowledgement from RabbitMQ before returning from the publish method.
If the acknowledgement isn't received within 15 seconds, an exception is thrown.
This behavior may be changed by using the following setter methods:

`
bus.setAcknowledgementTimeout(long timeoutInMilliseconds);

bus.setWaitForAcknowledgement(boolean waitForAck);
`

The serialized format for messages on the bus is currently JSON only.

The encoding for the JSON message defaults to UTF-8.
 