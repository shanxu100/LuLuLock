package com.luluteam.lululock.utils.mqtt;


import android.util.Log;

import com.luluteam.lululock.app.App;
import com.luluteam.lululock.utils.GsonUtil;
import com.luluteam.lululock.utils.ShowUtil;
import com.luluteam.lululock.utils.lock_screen.DeviceManager;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by guan on 5/20/17.
 * <p>
 * 处理 mqtt 接收的消息的类
 */

public class MessageHandler {

    private String TAG = "MessageHandler";

    //===============================================================================

    /**
     * 单例模式：静态内部类
     */
    private MessageHandler() {
    }

    public static class MessageHandlerBuilder {
        public final static MessageHandler messageHandler = new MessageHandler();
    }

    public static MessageHandler getInstance() {
        return MessageHandlerBuilder.messageHandler;
    }

    //===============================================================================

    /**
     * 重要：
     * 对mqtt消息的处理，在这里进行
     * <p>
     * 注意：运行此方法的进程，不是主线程，而是MQTTService中的Handler中的进程
     *
     * @param topic
     * @param mqttMessage
     */
    public void onMessage(String topic, MqttMessage mqttMessage) {

        if (topic.equals("TOPIC_LOCK_SCREEN")) {

            //首先解析成父类，获取Type字段
            MQTTConfig.MQTTMessage tmpMessage = GsonUtil.getGsonInstance().fromJson(mqttMessage.toString(),
                    MQTTConfig.MQTTMessage.class);
            //再根据type字段，分类讨论
            if (MQTTConfig.MessageType.STRING == tmpMessage.type) {

                MQTTConfig.StringMessage message = GsonUtil.getGsonInstance().fromJson(mqttMessage.toString(),
                        MQTTConfig.StringMessage.class);
                ShowUtil.LogAndToast("String");

            } else if (MQTTConfig.MessageType.ACTION_LOCK_SCREEN == tmpMessage.type) {

                if (tmpMessage.uuid.equals(App.uuid))
                {
                    Log.i(TAG,"收到本机发送的消息，忽略");
                    return;
                }

                MQTTConfig.LockScreenMessage message = GsonUtil.fromJson(mqttMessage.toString(),
                        MQTTConfig.LockScreenMessage.class);
                DeviceManager.getInstance().keepScreenLocked(message.keepLocked);

            } else if (MQTTConfig.MessageType.ACTION_CLICK_SCREEN == tmpMessage.type) {

                MQTTConfig.ClickScreenMessage message = GsonUtil.fromJson(mqttMessage.toString(),
                        MQTTConfig.ClickScreenMessage.class);
                int x = message.xInScreen;
                int y = message.yInScreen;

            }

        } else {
            Log.e(TAG,"未处理的话题"+ topic);
        }
    }
}
