package idealworld.dew.baas.gateway.process;

import com.ecfront.dew.common.StandardCode;
import idealworld.dew.baas.common.enumeration.AuthActionKind;
import idealworld.dew.baas.common.enumeration.ResourceKind;
import idealworld.dew.baas.gateway.GatewayConfig;
import idealworld.dew.baas.gateway.util.HttpClient;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

/**
 * 鉴权处理器
 *
 * @author gudaoxuri
 */
@Slf4j
public class DistributeHandler extends GatewayHandler {

    private final GatewayConfig.Request request;
    private final GatewayConfig.Distribute distribute;

    public DistributeHandler(GatewayConfig.Request request, GatewayConfig.Distribute distribute) {
        this.request = request;
        this.distribute = distribute;
    }

    @SneakyThrows
    @Override
    public void handle(RoutingContext ctx) {
        var identOptInfo = (IdentOptCacheInfo) ctx.get(CONTEXT_INFO);
        var resourceUri = (URI) ctx.get(request.getResourceUriKey());
        var action = (AuthActionKind) ctx.get(request.getActionKey());

        HttpMethod httpMethod = HttpMethod.GET;
        Buffer body = null;
        String host;
        Integer port;
        String path;
        switch (action) {
            case FETCH:
                httpMethod = HttpMethod.GET;
                break;
            case CREATE:
                httpMethod = HttpMethod.POST;
                body = ctx.getBody();
                break;
            case MODIFY:
                httpMethod = HttpMethod.PUT;
                body = ctx.getBody();
                break;
            case PATCH:
                httpMethod = HttpMethod.PATCH;
                body = ctx.getBody();
                break;
            case DELETE:
                httpMethod = HttpMethod.DELETE;
                break;
        }
        Future<HttpResponse<Buffer>> request;
        switch (ResourceKind.parse(resourceUri.getScheme().toLowerCase())) {
            case HTTP:
                request = HttpClient.request(httpMethod, resourceUri.toString(), body, null, distribute.getTimeoutMs());
                break;
            case MENU:
            case ELEMENT:
                request = HttpClient.request(httpMethod,
                        distribute.getIamServiceName(),
                        distribute.getIamServicePort(),
                        distribute.getIamServicePath(),
                        null,
                        body, null, distribute.getTimeoutMs());
                break;
            case RELDB:
                request = HttpClient.request(httpMethod,
                        distribute.getReldbServiceName(),
                        distribute.getReldbServicePort(),
                        distribute.getReldbServicePath(),
                        null,
                        body, null, distribute.getTimeoutMs());
                break;
            case CACHE:
                request = HttpClient.request(httpMethod,
                        distribute.getCacheServiceName(),
                        distribute.getCacheServicePort(),
                        distribute.getCacheServicePath(),
                        null,
                        body, null, distribute.getTimeoutMs());
                break;
            case MQ:
                request = HttpClient.request(httpMethod,
                        distribute.getMqServiceName(),
                        distribute.getMqServicePort(),
                        distribute.getMqServicePath(),
                        null,
                        body, null, distribute.getTimeoutMs());
                break;
            case OBJECT:
                request = HttpClient.request(httpMethod,
                        distribute.getObjServiceName(),
                        distribute.getObjServicePort(),
                        distribute.getObjServicePath(),
                        null,
                        body, null, distribute.getTimeoutMs());
                break;
            default:
                error(Integer.parseInt(StandardCode.NOT_FOUND.toString()), "资源类型不存在", ctx);
                return;
        }
        request.onSuccess(resp -> ctx.end(resp.body()))
                .onFailure(e -> error(Integer.parseInt(StandardCode.INTERNAL_SERVER_ERROR.toString()), e.getMessage(), ctx));
    }

}
