package service

import demo.SampleApi

/**
  * Created by Janos on 12/9/2015.
  */
class SampleApiImpl extends SampleApi {
  override def echo(name: String): String = s"Echoed: ${name}"
}
