package lu.itrust.adtop.tools.API;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.adtop.exception.ADException;
import lu.itrust.adtop.model.measure.MeasureContainer;

/**
 * Created by philippe on 30.03.15.
 */
public class ConnectorAPIImpl implements ConnectorAPI {

	public static final String JSON_ACCEPT = "accept";
	public static final String APPLICATION_JSON = "application/json";
	public static final String JSON_CHARSET = "charset";
	public static final String CHARSET_UTF_8 = "UTF-8";
	public static final String COOKIE = "Cookie";
	public static final String SET_COOKIE = "Set-Cookie";

	private String sessionId;

	private String credential;

	private String url;

	public ConnectorAPIImpl(String url) {
		this.url = url;

	}

	@Override
	public boolean connect(String username, String password) {
		Boolean authencated = false;
		try {
			HttpHost httpHost = new HttpHost(url);
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
			AuthCache authCache = new BasicAuthCache();
			authCache.put(httpHost, new BasicScheme());
			HttpClientContext httpClientContext = HttpClientContext.create();
			httpClientContext.setCredentialsProvider(credentialsProvider);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(url);
			credential = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
			httpGet.addHeader("Authorization", "Basic " + credential);
			HttpResponse response = httpClient.execute(httpGet);
			if ((authencated = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK))
				extractSessionId(response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!authencated)
				this.credential = null;
		}
		return authencated;
	}

	private void extractSessionId(HttpResponse response) {
		if (response.getFirstHeader(SET_COOKIE) != null)
			sessionId = response.getFirstHeader(SET_COOKIE).getValue().split(";", 2)[0];
	}

	@Override
	public boolean isConnected() {
		try {
			if (credential != null)
				return true;
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null, "IsAuthenticate", "GET", true));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), Boolean.TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public List<ApiNamable> getCustomer() {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null, "Api/data/customers", "GET", isConnected()));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<List<ApiNamable>>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return java.util.Collections.emptyList();
	}

	@Override
	public List<String> getCustomerIdString() {
		return this.getCustomer().stream().map(customer -> customer.getId().toString()).collect(Collectors.toList());
	}

	@Override
	public List<String> getCustomerNameString() {
		return this.getCustomer().stream().map(ApiNamable::getName).collect(Collectors.toList());
	}

	private HttpRequestBase prepareRequest(Object data, String subUrl, String method, boolean authenticated) throws UnsupportedEncodingException, JsonProcessingException {
		HttpRequestBase request = "POST".equalsIgnoreCase(method) ? new HttpPost(String.format("%s/%s", url, subUrl)) : new HttpGet(String.format("%s/%s", url, subUrl));
		request.setHeader(JSON_ACCEPT, APPLICATION_JSON);
		request.setHeader(JSON_CHARSET, CHARSET_UTF_8);
		request.setHeader("Content-type", "application/json;charset=UTF-8");
		if (authenticated && sessionId != null)
			request.setHeader(COOKIE, sessionId);
		else if (credential != null)
			request.setHeader("Authorization", "Basic " + credential);
		if (data != null && request instanceof HttpPost)
			((HttpPost) request).setEntity(new StringEntity(new ObjectMapper().writeValueAsString(data), "UTF-8"));
		return request;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;

	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public boolean logout() {
		this.sessionId = null;
		this.credential = null;
		return true;
	}

	public List<ApiNamable> getAnalysis(String customer) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null, "Api/data/analysis/all?customerId=" + customer, "GET", isConnected()));

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<List<ApiNamable>>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return java.util.Collections.emptyList();
	}

	public List<ApiNamable> getVersions(String customerId, String analysisId) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client
					.execute(prepareRequest(null, "Api/data/analysis/versions?customerId=" + customerId + "&identifier=" + analysisId.replace(" ", "%20"), "GET", true));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<List<ApiNamable>>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return java.util.Collections.emptyList();
	}

	public List<ApiNamable> getAssets(String versionId) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null, "Api/data/analysis/" + versionId + "/assets", "GET", true));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<List<ApiAsset>>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return java.util.Collections.emptyList();

	}

	private void checkAuthorisation(HttpResponse response) throws ADException {
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
			throw new ADException("ERROR.bad.credential", "Please check your credential");
		else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN)
			throw new ADException("ERROR.access.denied", "Access denied");
	}

	public List<ApiNamable> getScenarios(String versionId) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null, "Api/data/analysis/" + versionId + "/scenarios", "GET", true));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<List<ApiNamable>>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return java.util.Collections.emptyList();
	}

	public List<ApiNamable> getStandard(String versionId) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null, "Api/data/analysis/" + versionId + "/standards", "GET", true));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<List<ApiStandard>>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return java.util.Collections.emptyList();
	}

	public MeasureContainer getMeasureContainer(String analysisId, String assetId, String scenarioId, String standard) {
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(prepareRequest(null,
					"Api/data/load-rrf?analysisId=" + analysisId + "&scenarioId=" + scenarioId + "&assetId=" + assetId + "&standards=" + standard.replace(" ", "%20"), "GET",
					true));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				return new ObjectMapper().readValue(response.getEntity().getContent(), new TypeReference<MeasureContainer>() {
				});
			checkAuthorisation(response);
		} catch (ADException e) {
			throw e;
		} catch (Exception e) {
		}
		throw new ADException("ERROR.rrf.cannot.be.loaded", "RRF cannot be loaded");
	}

	public List<String> getStandardNameString(String versionId) {
		return this.getStandard(versionId).stream().map(ApiNamable::getName).collect(Collectors.toList());

	}

	@Override
	public boolean isAuthorized() {
		return getCustomer() != null;
	}

	public List<String> getAssetNameString(String versionId) {
		return this.getAssets(versionId).stream().map(ApiNamable::getName).collect(Collectors.toList());
	}

	public List<String> getAssetIdString(String versionId) {
		return this.getAssets(versionId).stream().map(name -> name.getId().toString()).collect(Collectors.toList());
	}

	public List<String> getAnalysisNameString(String custId) {
		return this.getAnalysis(custId).stream().map(ApiNamable::getName).collect(Collectors.toList());
	}

	public List<String> getAnalysisIdString(String custId) {
		return this.getAnalysis(custId).stream().map(name -> name.getId().toString()).collect(Collectors.toList());
	}

	public List<String> getVersionNameString(String customerId, String analysisId) {
		return this.getVersions(customerId, analysisId).stream().map(ApiNamable::getName).collect(Collectors.toList());
	}

	public List<String> getVersionIdString(String customerId, String analysisId) {
		return this.getVersions(customerId, analysisId).stream().map(name -> name.getId().toString()).collect(Collectors.toList());
	}

	public List<String> getScenariosNameString(String versionId) {
		return this.getScenarios(versionId).stream().map(ApiNamable::getName).collect(Collectors.toList());
	}

	public List<String> getScenariosIdString(String versionId) {
		return this.getScenarios(versionId).stream().map(name -> name.getId().toString()).collect(Collectors.toList());
	}

	public List<String> getStandardIdString(String versionId) {
		return this.getStandard(versionId).stream().map(name -> name.getId().toString()).collect(Collectors.toList());
	}

}