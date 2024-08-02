package it.pagopa.atmlayer.wf.task.service;

import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.client.bean.AuthParameters;
import it.pagopa.atmlayer.wf.task.client.bean.Token;

public interface TokenService {

	RestResponse<Token> generateToken(AuthParameters authParameters);

}
