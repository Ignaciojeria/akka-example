package com.akkaexample.akka.sample2;
import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;

public class Example2 {

    static class AlarmActor extends AbstractLoggingActor {

        private final String password;


        public AlarmActor(String password) {
            this.password = password;
        }

        /* protocol  */
        /* Activity class telling us if something happen in the house where it was intalled this Alarm */
        static class Activity {
        }

        /* if you can disable alarm you nedd a password */
        static class Disable {
            private final String password;

            public Disable(String password) {
                this.password = password;
            }
        }

        /* if you can enable alarm you nedd a password */
        static class Enable {
            private final String password;

            Enable(String password) {
                this.password = password;
            }
        }
        /* end protocol */

        public static Props props(String password) {
            return Props.create(AlarmActor.class, password);
        }

        private void onDisable(Disable disable) {
            if (password.equals(disable.password)) {
                log().info("alarm was disabled");

            } else {
                log().warning("someone who did not know the password tried to disable it");
            }
        }

        private void onEnable(Enable enable) {
            if (password.equals(enable.password)) {
                log().info("alarm was enabled");
            } else {
                log().info("someone failed enabled alarm");
            }
        }

        private void onActivity(Activity ignored) {
            log().warning("iuiuiuiu alarm alarm alarm!!!!");
        }

        @Override
        public Receive createReceive() {
            return ReceiveBuilder.create()
                    .match(Enable.class, this::onEnable)
                    .match(Activity.class, this::onActivity)
                    .match(Disable.class, this::onDisable)
                    .build();
        }
    }

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create();
        final ActorRef alarmActorRef = actorSystem.actorOf(AlarmActor.props("password2019"), "alarmactor");
        alarmActorRef.tell(new AlarmActor.Enable("password2018"), ActorRef.noSender());
        alarmActorRef.tell(new AlarmActor.Enable("password2019"), ActorRef.noSender());
        alarmActorRef.tell(new AlarmActor.Activity(), ActorRef.noSender());
        alarmActorRef.tell(new AlarmActor.Disable("paassword"), ActorRef.noSender());
        alarmActorRef.tell(new AlarmActor.Disable("password2019"), ActorRef.noSender());
    }
}
