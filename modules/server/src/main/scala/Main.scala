import cats.effect.*
import cats.syntax.flatMap.*
import protos.orders.Item
import cats.effect.*
import fs2.grpc.syntax.all.*
import io.grpc.*
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import protos.orders.*
import cats.instances.order

class OrderService extends OrderFs2Grpc[IO, Metadata]:
  override def sendOrderStream(
      request: fs2.Stream[IO, OrderRequest],
      ctx: Metadata,
  ): fs2.Stream[IO, OrderResponse] =
    request.map { req =>
      OrderResponse(
        req.orderID,
        req.items,
        req.items.map(_.amount).sum,
      )
    }

object OrderServer extends IOApp.Simple:
  val grpcServerResource: Resource[IO, Server] =
    OrderFs2Grpc
      .bindServiceResource(new OrderService)
      .flatMap { service =>
        NettyServerBuilder
          .forPort(8080)
          .addService(service)
          .resource[IO]
      }

  def run: IO[Unit] = grpcServerResource.use { service =>
    IO.println("Server started") *> IO.never
  }

object Client:
  val resource =
    NettyChannelBuilder.forAddress("localhost", 8080).usePlaintext.resource[IO] >>=
      OrderFs2Grpc.stubResource

object GrpcDemo extends IOApp.Simple:
  def run: IO[Unit] =
    Client.resource.use { orderService =>
      val orderStream = fs2.Stream.eval(IO.pure(OrderRequest(1, List(Item("iPhone", 2, 2000.0)))))

      orderService
        .sendOrderStream(orderStream, new Metadata())
        .compile
        .toList
        .flatMap(IO.println)

    }
