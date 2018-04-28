package io.toolsplus.atlassian.connect.play.slick

import javax.inject.{Inject, Singleton}
import io.toolsplus.atlassian.connect.play.api.models.{
  AtlassianHost,
  StandardAtlassianHost
}
import io.toolsplus.atlassian.connect.play.api.models.Predefined.ClientKey
import io.toolsplus.atlassian.connect.play.api.repositories.AtlassianHostRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.sql.SqlProfile.ColumnOption.{NotNull, SqlType}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SlickAtlassianHostRepository @Inject()(
    protected val dbConfigProvider: DatabaseConfigProvider)
    extends AtlassianHostRepository
    with AtlassianHostTable
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def all(): Future[Seq[AtlassianHost]] = db.run(hosts.result)

  def findByClientKey(clientKey: ClientKey): Future[Option[AtlassianHost]] =
    db.run(hosts.filter(_.clientKey === clientKey).result.headOption)

  def findByBaseUrl(baseUrl: String): Future[Option[AtlassianHost]] =
    db.run(hosts.filter(_.baseUrl === baseUrl).result.headOption)

  /** Saves the given Atlassian host by inserting it if it does not exist or
    * updating an existing record if it's already present.
    *
    * Note that Slick returns None if the record is updated and Some if it's
    * inserted. Override this behaviour to always return a host.
    *
    * @param host Atlassian Connect host to store.
    * @return Saved Atlassian Connect host.
    */
  def save(host: AtlassianHost): Future[AtlassianHost] = {
    db.run(hosts.insertOrUpdate(host)).map(_ => host)
  }
}

private[slick] trait AtlassianHostTable {

  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  lazy protected val hosts = TableQuery[Schema]

  private[AtlassianHostTable] class Schema(tag: Tag)
      extends Table[AtlassianHost](tag, "atlassian_host") {
    val clientKey = column[ClientKey]("client_key", O.PrimaryKey)
    val key = column[String]("key", NotNull)
    val publicKey =
      column[String]("public_key", NotNull, SqlType("VARCHAR(512)"))
    val oauthClientId = column[Option[String]]("oauth_client_id")
    val sharedSecret = column[String]("shared_secret", NotNull)
    val serverVersion = column[String]("server_version", NotNull)
    val pluginsVersion = column[String]("plugins_version", NotNull)
    val baseUrl = column[String]("base_url", NotNull, SqlType("VARCHAR(512)"))
    val productType = column[String]("product_type", NotNull)
    val description = column[String]("description", NotNull)
    val serviceEntitlementNumber =
      column[Option[String]]("service_entitlement_number")
    val installed = column[Boolean]("installed", NotNull)

    val clientKeyIndex =
      index("uq_ac_host_client_key", clientKey, unique = true)
    val baseUrlIndex = index("uq_ac_host_base_url", baseUrl, unique = true)

    def * =
      (clientKey,
       key,
       publicKey,
       oauthClientId,
       sharedSecret,
       serverVersion,
       pluginsVersion,
       baseUrl,
       productType,
       description,
       serviceEntitlementNumber,
       installed) <> (toHost.tupled, fromHost)

    private def toHost: (ClientKey,
                         String,
                         String,
                         Option[String],
                         String,
                         String,
                         String,
                         String,
                         String,
                         String,
                         Option[String],
                         Boolean) => AtlassianHost = StandardAtlassianHost.apply
  }

  private def fromHost: AtlassianHost => Option[
    (ClientKey,
     String,
     String,
     Option[String],
     String,
     String,
     String,
     String,
     String,
     String,
     Option[String],
     Boolean)] = { host: AtlassianHost =>
    StandardAtlassianHost.unapply(
      StandardAtlassianHost(
        host.clientKey,
        host.key,
        host.publicKey,
        host.oauthClientId,
        host.sharedSecret,
        host.serverVersion,
        host.pluginsVersion,
        host.baseUrl,
        host.productType,
        host.description,
        host.serviceEntitlementNumber,
        host.installed
      ))
  }

}
