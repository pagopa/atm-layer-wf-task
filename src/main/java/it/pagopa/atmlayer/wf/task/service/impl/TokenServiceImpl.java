package it.pagopa.atmlayer.wf.task.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.client.MilAuthClient;
import it.pagopa.atmlayer.wf.task.client.bean.AuthParameters;
import it.pagopa.atmlayer.wf.task.client.bean.RequestHeaders;
import it.pagopa.atmlayer.wf.task.client.bean.Token;
import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.enumerative.RequiredVariables;
import it.pagopa.atmlayer.wf.task.service.TokenService;
import it.pagopa.atmlayer.wf.task.util.Properties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class TokenServiceImpl implements TokenService {

	@Inject
	@RestClient
	MilAuthClient milWebClient;

	@Inject
	Properties properties;

	@Override
	public Uni<Token> generateToken(AuthParameters authParameters) {
		log.info("Mil auth request starting. . .");
		RequestHeaders headers = prepareAuthHeaders(authParameters);
		String body = prepareAuthBody(authParameters);
		log.info("Request ready, calling rest client on base URL: {}", System.getenv("MIL_BASE_PATH"));

		return milWebClient.getTokenFromMil(headers.getContentType(), headers.getRequestId(), headers.getAcquirerId(),
				headers.getChannel(), headers.getTerminalId(), body);
	}

	private String prepareAuthBody(AuthParameters authParameters) {
		Map<String, String> bodyParams = new HashMap<>();
		bodyParams.put(RequiredVariables.CLIENT_ID.getValue(), properties.milAuth().clientId());
		bodyParams.put(RequiredVariables.CLIENT_SECRET.getValue(), properties.milAuth().clientSecret());
		bodyParams.put(RequiredVariables.GRANT_TYPE.getValue(), properties.milAuth().grantType());
		if (authParameters.getFiscalCode() != null && !authParameters.getFiscalCode().isEmpty()) {
			bodyParams.put(RequiredVariables.FISCAL_CODE.getValue(), authParameters.getFiscalCode());
		}
		String body = bodyParams.entrySet().stream()
				.map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
						+ URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
				.collect(Collectors.joining("&"));
		return body;
	}

	private static RequestHeaders prepareAuthHeaders(AuthParameters authParameters) {
		RequestHeaders headers = new RequestHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setRequestId(UUID.randomUUID().toString());
		headers.setAcquirerId(authParameters.getAcquirerId());
		headers.setChannel(authParameters.getChannel());
		headers.setTerminalId(authParameters.getTerminalId());
		return headers;
	}

}
