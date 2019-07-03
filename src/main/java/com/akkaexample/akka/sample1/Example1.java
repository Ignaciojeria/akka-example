package com.akkaexample.akka.sample1;

import akka.actor.*;

public class Example1 {

    /*Documentación */
    //Hello World - >

    /*
     * "Akka es un toolkit, no un framework"

Un actor es una clase "regular"
pero hay una gran diferencia
en cómo interactuas con ella y en cómo
otros objectos interactúan con ellas.

La unica manera de comunicarse entre
los actores es a través de mensajes
y estos mensajes son inmutables.

cuando un actor recibe un mensaje puede
hacer lo siguiente :

- Mandar mensajes a otros actores.
- Puede mutar su propio estado (el del actor)
- Puede cambiar su compornamiento (como reacciona a los mensajes que siguen)
- Puede crear otros actores
      * */

    /* Actor Structure */
    static class CounterActor extends AbstractLoggingActor {
    /* This is a basic example of an mutable state inside actor */
        private int counter = 0;

        /* protocol */
        static class Message {
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(
                            Message.class, this::onMessage)
                    .matchAny(o -> log().info("received unknown message"))
                    .build();
        }


        private void onMessage(Message message) {
            counter++;
            log().info("increase counter" + counter);
        }

        /*this static method can create an actor -> https://doc.akka.io/api/akka/current/akka/actor/Props.html */
        public static Props props() {
            return Props.create(CounterActor.class);
        }

    }
    /* End Actor Structure */

    public static void main(String[] args) {

        /* note : we can not instanciate an actor as a regular object, we need an ActorSystem Object */
        /* start creating an actor */
        ActorSystem actorSystem = ActorSystem.create("sample1");
        final ActorRef counterActorRef = actorSystem.actorOf(CounterActor.props(), "counteractor");
        /* end creating an actor */

        /* tell method can send message to created actor. This is totally safe in a concurrent context*/
        counterActorRef.tell(new CounterActor.Message(), ActorRef.noSender());

        /* increase counter value inside the created actor using threads */
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                /* it is totally thread safe */
                counterActorRef.tell(new CounterActor.Message(), ActorRef.noSender());
            }).start();
        }
        /* end increase counter value inside the created actor using threads */

    }


}
