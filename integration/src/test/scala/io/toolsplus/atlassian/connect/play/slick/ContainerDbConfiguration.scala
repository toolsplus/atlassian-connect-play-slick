package io.toolsplus.atlassian.connect.play.slick

import com.dimafeng.testcontainers.PostgreSQLContainer

object ContainerDbConfiguration {

  def configuration(container: PostgreSQLContainer): Map[String, Any] =
    Map(
      "slick.dbs.default.profile" -> "slick.jdbc.PostgresProfile$",
      "slick.dbs.default.db.driver" -> "org.postgresql.Driver",
      "slick.dbs.default.db.url" -> container.jdbcUrl,
      "slick.dbs.default.db.user" -> container.username,
      "slick.dbs.default.db.password" -> container.password,
      "play.evolutions.db.default.enabled" -> true
    )

}
