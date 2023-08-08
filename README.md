# Atlassian Connect Play Slick

[![Continuous integration](https://github.com/toolsplus/atlassian-connect-play-slick/actions/workflows/ci.yml/badge.svg)](https://github.com/toolsplus/atlassian-connect-play-slick/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/toolsplus/atlassian-connect-play-slick/branch/master/graph/badge.svg)](https://codecov.io/gh/toolsplus/atlassian-connect-play-slick)
[![Maven Central](https://img.shields.io/maven-central/v/io.toolsplus/atlassian-connect-play-slick_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/io.toolsplus/atlassian-connect-play-slick_2.13)

Atlassian Connect Play Slick is a Play module providing Slick 
implementation for data repositories defined in [Atlassian Connect Play](atlassian-connect-play).

## Quick start

atlassian-connect-play-slick is published to Maven Central for Scala 2.13 and 
Play 2.8.x, so you can just add the following to your build:

    libraryDependencies += "io.toolsplus" %% "atlassian-connect-play-slick" % "x.x.x"

### JDBC driver dependency
The Play Slick module does not bundle any JDBC driver. Hence, you will need to 
explicitly add the JDBC driver(s) you want to use in your application. 

For instance, if you would like to use an in-memory database such as H2, you will 
have to add a dependency to it:

    libraryDependencies += "com.h2database" % "h2" % "${H2_VERSION}"

After that follow the [Play Slick](play-slick-docs) documentation on how to add 
the Slick database configuration to your `application.conf`. E.g.

    slick.dbs.default.profile"="slick.jdbc.H2Profile$
    slick.dbs.default.db.driver="org.h2.Driver"
    slick.dbs.default.db.url="jdbc:h2:mem:play"

That's it! You now have a Slick database backend to store Atlassian hosts.

### Using Play Evolutions

Play Slick supports Play database evolutions.

To enable evolutions, you will need the following dependencies:

    libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "${PLAY_SLICK_VERSION}"
    
Note there is no need to add the Play evolutions component to your dependencies, 
as it is a transitive dependency of the `play-slick-evolutions` module.

Finally add the evolutions configuration to your `application.conf`:

    evolutions {
        db {
            default {
                enabled = true
                autoApply = true
                autocommit = false
                useLocks = true
            }
        }
    }
    
Refer to the [Play database evolutions guide](play-evolutions) on configuration details.

## Contributing
 
Pull requests are always welcome. Please follow the [contribution guidelines](CONTRIBUTING.md).
    
## License

atlassian-connect-play-slick is licensed under the **[Apache License, Version 2.0][apache]** (the
"License"); you may not use this software except in compliance with the License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[atlassian-connect-play]: https://github.com/toolsplus/atlassian-connect-play
[play-evolutions]: https://www.playframework.com/documentation/2.8.x/Evolutions
[apache]: https://www.apache.org/licenses/LICENSE-2.0
[play-slick-docs]: https://www.playframework.com/documentation/2.8.x/PlaySlick
