package io.toolsplus.atlassian.connect.play.slick

import org.scalatest.Suites

/**
  * Combines the repository tests into a single suite to ensure individual
  * repository test suites run sequentially.
  *
  * If we attempt to run multiple repository suites in parallel, the evolutions
  * script will fail. An alternative solution might be to use different db
  * configurations for each repository test suite to isolate them.
  */
class SlickRepositoryIt
    extends Suites(new SlickAtlassianHostRepositoryIt,
                   new SlickForgeInstallationRepositoryIt,
                   new SlickForgeSystemAccessTokenRepositoryIt)
