package crylio.rest.controllers

import crylio.common.akka.StartupActor
import crylio.rest.Errors.ErrorResult

trait Controller extends StartupActor {
  def failure(result: ErrorResult): Unit = failure(result.toRestException)
}
