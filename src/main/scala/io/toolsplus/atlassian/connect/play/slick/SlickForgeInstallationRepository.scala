package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.api.models.Predefined.ClientKey
import io.toolsplus.atlassian.connect.play.api.models.{
  DefaultForgeInstallation,
  ForgeInstallation
}
import io.toolsplus.atlassian.connect.play.api.repositories.ForgeInstallationRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.sql.SqlProfile.ColumnOption.NotNull

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SlickForgeInstallationRepository @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
) extends ForgeInstallationRepository
    with ForgeInstallationTable
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def all(): Future[Seq[ForgeInstallation]] = db.run(installations.result)

  def findByInstallationId(
      installationId: String
  ): Future[Option[ForgeInstallation]] =
    db.run(
      installations
        .filter(_.installationId === installationId)
        .result
        .headOption
    )

  def findByClientKey(clientKey: ClientKey): Future[Seq[ForgeInstallation]] =
    db.run(installations.filter(_.clientKey === clientKey).result)

  /** Saves the given Forge installation by inserting it if it does not exist or
    * updating an existing record if it's already present.
    *
    * Note that Slick returns None if the record is updated and Some if it's
    * inserted. Override this behaviour to always return an installation.
    *
    * @param installation
    *   Forge installation to store.
    * @return
    *   Saved Forge installation.
    */
  def save(installation: ForgeInstallation): Future[ForgeInstallation] =
    db.run(installations.insertOrUpdate(installation)).map(_ => installation)

  /** Delete all Forge installations associated with a specific clientKey.
    *
    * @param clientKey
    *   Client key of the Atlassian Connect host
    * @return
    *   Number of deleted Forge installations
    */
  def deleteByClientKey(clientKey: ClientKey): Future[Int] =
    db.run(installations.filter(_.clientKey === clientKey).delete)
}

private[slick] trait ForgeInstallationTable {

  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  lazy protected val installations = TableQuery[Schema]

  private[ForgeInstallationTable] class Schema(tag: Tag)
      extends Table[ForgeInstallation](tag, "forge_installation") {
    val installationId = column[String]("installation_id", O.PrimaryKey)
    val clientKey = column[ClientKey]("client_key", NotNull)

    val installationIdIndex =
      index(
        "uq_forge_installation_installation_id",
        installationId,
        unique = true
      )

    def * =
      (installationId, clientKey) <> (toInstallation.tupled, fromInstallation)

    private def toInstallation: (String, ClientKey) => ForgeInstallation =
      DefaultForgeInstallation.apply
  }

  private def fromInstallation
      : ForgeInstallation => Option[(String, ClientKey)] = {
    (installation: ForgeInstallation) =>
      Some(installation.installationId, installation.clientKey)
  }

}
