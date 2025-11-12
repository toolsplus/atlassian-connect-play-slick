package io.toolsplus.atlassian.connect.play.slick

import io.toolsplus.atlassian.connect.play.api.models.{
  DefaultForgeSystemAccessToken,
  ForgeSystemAccessToken
}
import io.toolsplus.atlassian.connect.play.api.repositories.ForgeSystemAccessTokenRepository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.sql.SqlProfile.ColumnOption.NotNull

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SlickForgeSystemAccessTokenRepository @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
) extends ForgeSystemAccessTokenRepository
    with ForgeSystemAccessTokenTable
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def all(): Future[Seq[ForgeSystemAccessToken]] =
    db.run(systemAccessTokens.result)

  override def save(
      token: ForgeSystemAccessToken
  ): Future[ForgeSystemAccessToken] =
    db.run(systemAccessTokens.insertOrUpdate(token)).map(_ => token)

  override def findByInstallationId(
      installationId: String
  ): Future[Option[ForgeSystemAccessToken]] =
    db.run(
      systemAccessTokens
        .filter(t => t.installationId === installationId)
        .result
        .headOption
    )

  override def findByInstallationIdAndExpirationTimeAfter(
      installationId: String,
      expirationTime: Instant
  ): Future[Option[ForgeSystemAccessToken]] =
    db.run(
      systemAccessTokens
        .filter(t =>
          t.installationId === installationId && t.expirationTime >= expirationTime
        )
        .result
        .headOption
    )

  override def deleteAllByExpirationTimeBefore(
      expirationTime: Instant
  ): Future[Int] =
    db.run(systemAccessTokens.filter(_.expirationTime < expirationTime).delete)
}

private[slick] trait ForgeSystemAccessTokenTable {

  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  lazy protected val systemAccessTokens = TableQuery[Schema]

  private[ForgeSystemAccessTokenTable] class Schema(tag: Tag)
      extends Table[ForgeSystemAccessToken](tag, "forge_system_access_token") {
    val installationId = column[String]("installation_id", O.PrimaryKey)
    val apiBaseUrl = column[String]("api_base_url", NotNull)
    val accessToken = column[String]("access_token", NotNull)
    val expirationTime = column[Instant]("expiration_time", NotNull)

    val expirationTimeIndex =
      index("forge_system_access_token_expiration_time", expirationTime)

    def * =
      (installationId, apiBaseUrl, accessToken, expirationTime) <> (
        toSystemAccessToken.tupled,
        fromSystemAccessToken
      )

    private def toSystemAccessToken
        : (String, String, String, Instant) => ForgeSystemAccessToken =
      DefaultForgeSystemAccessToken.apply
  }

  private def fromSystemAccessToken
      : ForgeSystemAccessToken => Option[(String, String, String, Instant)] = {
    (systemAccessToken: ForgeSystemAccessToken) =>
      Some(
        systemAccessToken.installationId,
        systemAccessToken.apiBaseUrl,
        systemAccessToken.accessToken,
        systemAccessToken.expirationTime
      )
  }

}
