package monix.kafka

import java.io.File
import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import monix.kafka.config.{AutoOffsetReset, SSLProtocol, SecurityProtocol}

import scala.concurrent.duration._

/** Configuration for Kafka Consumer.
  *
  * For the official documentation on the available configuration
  * options, see
  * [[https://kafka.apache.org/documentation.html#consumerconfigs Consumer Configs]]
  * on `kafka.apache.org`.
  *
  * @param servers is the `bootstrap.servers` setting,
  *        a list of host/port pairs to use for establishing
  *        the initial connection to the Kafka cluster.
  *
  * @param fetchMinBytes is the `fetch.min.bytes` setting,
  *        the minimum amount of data the server should return
  *        for a fetch request.
  *
  * @param groupId is the `group.id` setting, a unique string
  *        that identifies the consumer group this consumer
  *        belongs to.
  *
  * @param heartbeatInterval is the `heartbeat.interval.ms` setting,
  *        the expected time between heartbeats to the consumer coordinator
  *        when using Kafka's group management facilities.
  *
  * @param maxPartitionFetchBytes is the `max.partition.fetch.bytes`
  *        setting, the maximum amount of data per-partition the
  *        server will return.
  *
  * @param sessionTimeout is the `session.timeout.ms` setting,
  *        the timeout used to detect failures when using Kafka's
  *        group management facilities.
  *
  * @param sslKeyPassword is the `ssl.key.password` setting and represents
  *        the password of the private key in the key store file.
  *        This is optional for client.
  *
  * @param sslKeyStorePassword is the `ssl.keystore.password` setting,
  *        being the password of the private key in the key store file.
  *        This is optional for client.
  *
  * @param sslKeyStoreLocation is the `ssl.keystore.location` setting and
  *        represents the location of the key store file. This is optional
  *        for client and can be used for two-way authentication for client.
  *
  * @param sslTrustStoreLocation is the `ssl.truststore.location` setting
  *        and is the location of the trust store file.
  *
  * @param sslTrustStorePassword is the `ssl.truststore.password` setting
  *        and is the password for the trust store file.
  *
  * @param autoOffsetReset is the `auto.offset.reset` setting,
  *        specifying what to do when there is no initial offset in
  *        Kafka or if the current offset does not exist any more
  *        on the server (e.g. because that data has been deleted).
  *
  * @param connectionsMaxIdleTime is the `connections.max.idle.ms` setting
  *        and specifies how much time to wait before closing idle connections.
  *
  * @param enableAutoCommit is the `enable.auto.commit` setting.
  *        If true the consumer's offset will be periodically committed
  *        in the background.
  *
  * @param excludeInternalTopics is the `exclude.internal.topics` setting.
  *        Whether records from internal topics (such as offsets) should be
  *        exposed to the consumer. If set to true the only way to receive
  *        records from an internal topic is subscribing to it.
  *
  * @param maxPollRecords is the `max.poll.records` setting, the
  *        maximum number of records returned in a single call to poll().
  *
  * @param receiveBufferInBytes is the `receive.buffer.bytes` setting,
  *        the size of the TCP receive buffer (SO_RCVBUF) to use
  *        when reading data.
  *
  * @param requestTimeout is the `request.timeout.ms` setting,
  *        The configuration controls the maximum amount of time
  *        the client will wait for the response of a request.
  *        If the response is not received before the timeout elapses
  *        the client will resend the request if necessary or fail the
  *        request if retries are exhausted.
  *
  * @param saslKerberosServiceName is the `sasl.kerberos.service.name` setting,
  *        being the Kerberos principal name that Kafka runs as.
  *
  * @param saslMechanism is the `sasl.mechanism` setting, being the SASL
  *        mechanism used for client connections. This may be any mechanism
  *        for which a security provider is available.
  *
  * @param securityProtocol is the `security.protocol` setting,
  *        being the protocol used to communicate with brokers.
  *
  * @param sendBufferInBytes is the `send.buffer.bytes` setting,
  *        being the size of the TCP send buffer (SO_SNDBUF) to use
  *        when sending data.
  *
  * @param sslEnabledProtocols is the `ssl.enabled.protocols` setting,
  *        being the list of protocols enabled for SSL connections.
  *
  * @param sslKeystoreType is the `ssl.keystore.type` setting,
  *        being the file format of the key store file.
  *
  * @param sslProtocol is the `ssl.protocol` setting,
  *        being the SSL protocol used to generate the SSLContext.
  *        Default setting is TLS, which is fine for most cases.
  *        Allowed values in recent JVMs are TLS, TLSv1.1 and TLSv1.2. SSL,
  *        SSLv2 and SSLv3 may be supported in older JVMs, but their usage
  *        is discouraged due to known security vulnerabilities.
  *
  * @param sslProvider is the `ssl.provider` setting,
  *        being the name of the security provider used for SSL connections.
  *        Default value is the default security provider of the JVM.
  *
  * @param sslTruststoreType is the `ssl.truststore.type` setting, being
  *        the file format of the trust store file.
  *
  * @param checkCRCs is the `check.crcs` setting, specifying to
  *        automatically check the CRC32 of the records consumed.
  *        This ensures no on-the-wire or on-disk corruption to the
  *        messages occurred. This check adds some overhead, so it may
  *        be disabled in cases seeking extreme performance.
  *
  * @param clientId is the `client.id` setting,
  *        an id string to pass to the server when making requests.
  *        The purpose of this is to be able to track the source of
  *        requests beyond just ip/port by allowing a logical application
  *        name to be included in server-side request logging.
  *
  * @param fetchMaxWaitTime is the `fetch.max.wait.ms` setting,
  *        the maximum amount of time the server will block before
  *        answering the fetch request if there isn't sufficient data to
  *        immediately satisfy the requirement given by fetch.min.bytes.
  *
  * @param metadataMaxAge is the `metadata.max.age.ms` setting.
  *        The period of time in milliseconds after which we force a
  *        refresh of metadata even if we haven't seen any partition
  *        leadership changes to proactively discover any new brokers
  *        or partitions.
  *
  * @param reconnectBackoffTime is the `reconnect.backoff.ms` setting.
  *        The amount of time to wait before attempting to reconnect to a
  *        given host. This avoids repeatedly connecting to a host in a
  *        tight loop. This backoff applies to all requests sent by the
  *        consumer to the broker.
  *
  * @param retryBackoffTime is the `retry.backoff.ms` setting.
  *        The amount of time to wait before attempting to retry a failed
  *        request to a given topic partition. This avoids repeatedly
  *        sending requests in a tight loop under some failure scenarios.
  */
final case class KafkaConsumerConfig(
  servers: List[String],
  fetchMinBytes: Int,
  groupId: String,
  heartbeatInterval: FiniteDuration,
  maxPartitionFetchBytes: Int,
  sessionTimeout: FiniteDuration,
  sslKeyPassword: Option[String],
  sslKeyStorePassword: Option[String],
  sslKeyStoreLocation: Option[String],
  sslTrustStoreLocation: Option[String],
  sslTrustStorePassword: Option[String],
  autoOffsetReset: AutoOffsetReset,
  connectionsMaxIdleTime: FiniteDuration,
  enableAutoCommit: Boolean,
  excludeInternalTopics: Boolean,
  maxPollRecords: Int,
  receiveBufferInBytes: Int,
  requestTimeout: FiniteDuration,
  saslKerberosServiceName: Option[String],
  saslMechanism: String,
  securityProtocol: SecurityProtocol,
  sendBufferInBytes: Int,
  sslEnabledProtocols: List[SSLProtocol],
  sslKeystoreType: String,
  sslProtocol: SSLProtocol,
  sslProvider: Option[String],
  sslTruststoreType: String,
  checkCRCs: Boolean,
  clientId: String,
  fetchMaxWaitTime: FiniteDuration,
  metadataMaxAge: FiniteDuration,
  reconnectBackoffTime: FiniteDuration,
  retryBackoffTime: FiniteDuration) {

  def toMap: Map[String,String] = Map(
    "bootstrap.servers" -> servers.mkString(","),
    "fetch.min.bytes" -> fetchMinBytes.toString,
    "group.id" -> groupId,
    "heartbeat.interval.ms" -> heartbeatInterval.toMillis.toString,
    "max.partition.fetch.bytes" -> maxPartitionFetchBytes.toString,
    "session.timeout.ms" -> sessionTimeout.toMillis.toString,
    "ssl.key.password" -> sslKeyPassword.orNull,
    "ssl.keystore.password" -> sslKeyStorePassword.orNull,
    "ssl.keystore.location" -> sslKeyStoreLocation.orNull,
    "ssl.truststore.password" -> sslTrustStorePassword.orNull,
    "ssl.truststore.location" -> sslTrustStoreLocation.orNull,
    "auto.offset.reset" -> autoOffsetReset.id,
    "connections.max.idle.ms" -> connectionsMaxIdleTime.toMillis.toString,
    "enable.auto.commit" -> enableAutoCommit.toString,
    "exclude.internal.topics" -> excludeInternalTopics.toString,
    "max.poll.records" -> maxPollRecords.toString,
    "receive.buffer.bytes" -> receiveBufferInBytes.toString,
    "request.timeout.ms" -> requestTimeout.toMillis.toString,
    "sasl.kerberos.service.name" -> saslKerberosServiceName.orNull,
    "sasl.mechanism" -> saslMechanism,
    "security.protocol" -> securityProtocol.id,
    "send.buffer.bytes" -> sendBufferInBytes.toString,
    "ssl.enabled.protocols" -> sslEnabledProtocols.map(_.id).mkString(","),
    "ssl.keystore.type" -> sslKeystoreType,
    "ssl.protocol" -> sslProtocol.id,
    "ssl.provider" -> sslProvider.orNull,
    "ssl.truststore.type" -> sslTruststoreType,
    "check.crcs" -> checkCRCs.toString,
    "client.id" -> clientId,
    "fetch.max.wait.ms" -> fetchMaxWaitTime.toMillis.toString,
    "metadata.max.age.ms" -> metadataMaxAge.toMillis.toString,
    "reconnect.backoff.ms" -> reconnectBackoffTime.toMillis.toString,
    "retry.backoff.ms" -> retryBackoffTime.toMillis.toString
  )

  def toProperties: Properties = {
    val props = new Properties()
    for ((k,v) <- toMap; if v != null) props.put(k,v)
    props
  }
}

object KafkaConsumerConfig {
  def apply(config: Config): KafkaConsumerConfig = {
    def getOptString(path: String): Option[String] =
      if (config.hasPath(path)) Option(config.getString(path))
      else None

    KafkaConsumerConfig(
      servers = config.getString("kafka.bootstrap.servers").trim.split("\\s*,\\s*").toList,
      fetchMinBytes = config.getInt("kafka.fetch.min.bytes"),
      groupId = config.getString("kafka.group.id"),
      heartbeatInterval = config.getInt("kafka.heartbeat.interval.ms").millis,
      maxPartitionFetchBytes = config.getInt("kafka.max.partition.fetch.bytes"),
      sessionTimeout = config.getInt("kafka.session.timeout.ms").millis,
      sslKeyPassword = getOptString("kafka.ssl.key.password"),
      sslKeyStorePassword = getOptString("kafka.ssl.keystore.password"),
      sslKeyStoreLocation = getOptString("kafka.ssl.keystore.location"),
      sslTrustStorePassword = getOptString("kafka.ssl.truststore.password"),
      sslTrustStoreLocation = getOptString("kafka.ssl.truststore.location"),
      autoOffsetReset = AutoOffsetReset(config.getString("kafka.auto.offset.reset")),
      connectionsMaxIdleTime = config.getInt("kafka.connections.max.idle.ms").millis,
      enableAutoCommit = config.getBoolean("kafka.enable.auto.commit"),
      excludeInternalTopics = config.getBoolean("kafka.exclude.internal.topics"),
      maxPollRecords = config.getInt("kafka.max.poll.records"),
      receiveBufferInBytes = config.getInt("kafka.receive.buffer.bytes"),
      requestTimeout = config.getInt("kafka.request.timeout.ms").millis,
      saslKerberosServiceName = getOptString("kafka.sasl.kerberos.service.name"),
      saslMechanism = config.getString("kafka.sasl.mechanism"),
      securityProtocol = SecurityProtocol(config.getString("kafka.security.protocol")),
      sendBufferInBytes = config.getInt("kafka.send.buffer.bytes"),
      sslEnabledProtocols = config.getString("kafka.ssl.enabled.protocols").split("\\s*,\\s*").map(SSLProtocol.apply).toList,
      sslKeystoreType = config.getString("kafka.ssl.keystore.type"),
      sslProtocol = SSLProtocol(config.getString("kafka.ssl.protocol")),
      sslProvider = getOptString("kafka.ssl.provider"),
      sslTruststoreType = config.getString("kafka.ssl.truststore.type"),
      checkCRCs = config.getBoolean("kafka.check.crcs"),
      clientId = config.getString("kafka.client.id"),
      fetchMaxWaitTime = config.getInt("kafka.fetch.max.wait.ms").millis,
      metadataMaxAge = config.getInt("kafka.metadata.max.age.ms").millis,
      reconnectBackoffTime = config.getInt("kafka.reconnect.backoff.ms").millis,
      retryBackoffTime = config.getInt("kafka.retry.backoff.ms").millis
    )
  }

  lazy val default: KafkaConsumerConfig =
    apply(ConfigFactory.load("monix/kafka/default.conf"))

  def load(): KafkaConsumerConfig =
    Option(System.getProperty("config.file")).map(f => new File(f)) match {
      case Some(file) if file.exists() =>
        loadFile(file, includeDefaults = true)
      case None =>
        Option(System.getProperty("config.resource")) match {
          case Some(resource) =>
            loadResource(resource, includeDefaults = true)
          case None =>
            default
        }
    }

  def loadResource(resourceBaseName: String, includeDefaults: Boolean = true): KafkaConsumerConfig = {
    def default = ConfigFactory.load("monix/kafka/default.conf")
    val config = ConfigFactory.load(resourceBaseName)
    if (!includeDefaults) apply(config) else
      apply(config.withFallback(default))
  }

  def loadFile(file: File, includeDefaults: Boolean = true): KafkaConsumerConfig = {
    def default = ConfigFactory.load("monix/kafka/default.conf")
    val config = ConfigFactory.parseFile(file).resolve()
    if (!includeDefaults) apply(config) else
      apply(config.withFallback(default))
  }
}
