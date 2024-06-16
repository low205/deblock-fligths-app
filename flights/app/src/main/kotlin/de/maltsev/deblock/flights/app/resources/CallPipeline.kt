package de.maltsev.deblock.flights.app.resources

import io.ktor.server.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

typealias CallPipeline = PipelineContext<Unit, ApplicationCall>
