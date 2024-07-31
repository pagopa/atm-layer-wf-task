package it.pagopa.atmlayer.wf.task.service;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.client.bean.AuthParameters;
import it.pagopa.atmlayer.wf.task.client.bean.Token;

public interface TokenService {

	Uni<Token> generateToken(AuthParameters authParameters);

}
