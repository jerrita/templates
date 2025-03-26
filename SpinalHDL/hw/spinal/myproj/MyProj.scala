package myproj

import spinal.lib._
import spinal.core._

case class SnailClkConfig(
    WIDTH: Int = 20
)

case class SnailClk(conf: SnailClkConfig) extends Bundle {
  val tick = Bool()
  val cnt  = Reg(UInt(conf.WIDTH bits)) init 0

  cnt  := cnt + 1
  tick := cnt(conf.WIDTH - 1)
}

case class LedTop(conf: SnailClkConfig) extends Component {
  val io = new Bundle {
    val led   = out Bits (8 bits)
    val sw3   = in Bits (7 bits)
    val led_g = out Bool ()
  }

  val blinker = new Area {
    val en     = io.sw3(5)
    val report = io.sw3(6)
    val cnt    = Reg(UInt(8 bits)) init 0
    val snail  = SnailClk(conf)

    when(snail.tick.rise & en) {
      cnt := cnt + 1
    }

    io.led   := ~cnt.asBits
    io.led_g := ~(cnt.andR && report)
  }
}

object GenVerilog extends App {
  Config.spinal.generateVerilog(LedTop(SnailClkConfig()))
}
