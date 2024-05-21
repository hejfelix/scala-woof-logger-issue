package com.may

import cats.effect.kernel.Resource
import cats.effect.std.Dispatcher
import cats.effect.{IO, IOApp}
import com.comcast.ip4s.*
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.legogroup.woof.Logger.*
import org.legogroup.woof.slf4j2.registerSlf4j
import org.legogroup.woof.{*, given}

object main extends IOApp.Simple:
  case class Agent(accountId: Long, agent: Long)

  given Filter = Filter.everything
  given Printer = ColorPrinter()

  def run: IO[Unit] =
    for
      given Logger[IO] <- DefaultLogger.makeIo(Output.fromConsole)
      _ <- runWithLogger
        .withLogContext("application", "app")
        .withLogContext("environment", "dev")
    yield ()

  private def runWithLogger(using logger: Logger[IO]): IO[Unit] = for {
    _ <- logger.info(s"Starting app")
    _ <- Stream
      .resource(resources)
      .flatMap { httpServer =>
        Stream.eval(logger.warn("Before exception")) ++
          Stream.eval(httpServer.useForever).concurrently {
            throw new RuntimeException("Catch me and log to console")
          }
      }
      .handleErrorWith { e =>
        Stream.eval {
          logger.error(s"An error occured: $e") // TODO: doesn't print anything
        }
      }
      .compile
      .drain
  } yield ()

  private def resources(using
      logger: Logger[IO]
  ): Resource[IO, (Resource[IO, Server])] =
    for
      given Dispatcher[IO] <- Dispatcher.sequential[IO]
      _ <- Logger[IO].info(s"Initializing resources").toResource
      _ <- logger.registerSlf4j.toResource
      httpServer = EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withPort(port"9006")
        .withHttpApp(
          HttpRoutes
            .of[IO] {
              case GET -> Root / "health" => Ok("works")
              case _                      => Ok("works")
            }
            .orNotFound
        )
        .build
    yield httpServer
