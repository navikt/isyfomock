package no.nav.syfo.mq

import com.ibm.mq.constants.CMQC.MQENC_NATIVE
import com.ibm.msg.client.jms.JmsConstants.WMQ_PROVIDER
import com.ibm.msg.client.jms.JmsFactoryFactory
import com.ibm.msg.client.wmq.common.CommonConstants.*
import no.nav.syfo.*
import javax.jms.ConnectionFactory
import javax.jms.JMSContext

private const val UTF_8_WITH_PUA = 1208

class MQSender(
    private val environmentMQ: EnvironmentMQ,
    private val serviceUser: ServiceUser,
) {
    private val jmsContext: JMSContext = connectionFactory().createContext()

    protected fun finalize() {
        try {
            jmsContext.close()
        } catch (e: Exception) {
            log.warn("Got exception when closing MQ-connection", e)
        }
    }

    fun send(payload: String) {
        jmsContext.createContext(AUTO_ACKNOWLEDGE).use { context ->
            val destination = context.createQueue("queue:///${environmentMQ.padm2Queuename}")
            val message = context.createTextMessage(payload)
            context.createProducer().send(destination, message)
        }
    }

    private fun connectionFactory(): ConnectionFactory {
        return JmsFactoryFactory.getInstance(WMQ_PROVIDER).createConnectionFactory().apply {
            setIntProperty(WMQ_CONNECTION_MODE, WMQ_CM_CLIENT)
            setStringProperty(WMQ_QUEUE_MANAGER, environmentMQ.mqQueueManager)
            setStringProperty(WMQ_HOST_NAME, environmentMQ.mqHostname)
            setStringProperty(WMQ_APPLICATIONNAME, environmentMQ.mqApplicationName)
            setIntProperty(WMQ_PORT, environmentMQ.mqPort)
            setStringProperty(WMQ_CHANNEL, environmentMQ.mqChannelName)
            setIntProperty(WMQ_CCSID, UTF_8_WITH_PUA)
            setIntProperty(JMS_IBM_ENCODING, MQENC_NATIVE)
            setIntProperty(JMS_IBM_CHARACTER_SET, UTF_8_WITH_PUA)
            setBooleanProperty(USER_AUTHENTICATION_MQCSP, true)
            setStringProperty(USERID, serviceUser.username)
            setStringProperty(PASSWORD, serviceUser.password)
        }
    }
}
