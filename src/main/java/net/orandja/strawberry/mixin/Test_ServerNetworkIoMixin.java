package net.orandja.strawberry.mixin;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;

@Mixin(ServerNetworkIo.class)
public class Test_ServerNetworkIoMixin {

    @Redirect(method = "bind", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/ServerBootstrap;childHandler(Lio/netty/channel/ChannelHandler;)Lio/netty/bootstrap/ServerBootstrap;"))
    public ServerBootstrap replaceChannelHander(ServerBootstrap instance, ChannelHandler childHandler) {
        instance.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                Method[] methods = childHandler.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if(methods[i].getName().equals("initChannel")) {
                        methods[i].setAccessible(true);
                        methods[i].invoke(childHandler, channel);
                        return;
                    }
                }
            }
        });
        return instance;
    }

}
