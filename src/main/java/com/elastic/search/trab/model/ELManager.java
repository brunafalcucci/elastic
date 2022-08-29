/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elastic.search.trab.model;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortOrder;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.core.TimeValue;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.MatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.sort.FieldSortBuilder;
//import org.elasticsearch.search.sort.ScoreSortBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author flavio
 */
public class ELManager implements AutoCloseable {

	private final RestHighLevelClient client;
	private final String idxName;

	public ELManager(String host, int port, String idxName) {

		this.idxName = idxName;

		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials("admin", "admin"));

		RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "https"))
				.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					}
				});

		this.client = new RestHighLevelClient(builder);
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

	public JSONArray search(String must, String must_not, String should, int page, int page_size) {

		//construindo uma bool query
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

		//adicionando o valor obrigatorio na busca (campo must)
		MatchQueryBuilder m = new MatchQueryBuilder("content", must);
		boolQueryBuilder.must(m);

		//adicionando o valor que nao deve estar presente na busca (campo must not)
		MatchQueryBuilder m2 = new MatchQueryBuilder("content", must_not);
		boolQueryBuilder.mustNot(m2);

		//adicionando o valor que pontua caso esteja presente (campo should)
		MatchQueryBuilder m3 = new MatchQueryBuilder("content", should);
		boolQueryBuilder.should(m3);

		//configuracoes do highlight de termos, para producao do resumo
		String[] preTags = new String[]{"<mark>"};
		String[] postTags = new String[]{"</mark>"};
		HighlightBuilder.Field hl
				= new HighlightBuilder.Field("content");
		hl.fragmentSize(400);
		hl.numOfFragments(1);
		hl.preTags(preTags);
		hl.postTags(postTags);
		HighlightBuilder hlBuilder = new HighlightBuilder();
		hlBuilder.field(hl);

		//construcao do objeto busca
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//anexando a bool query e o highlight de termos
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.highlighter(hlBuilder);

		//definindo a pagina a ser retornada
		searchSourceBuilder.from(
				(page - 1) * page_size);
		searchSourceBuilder.size(
				page_size);


		//filtrando para retornar somente paginas com id maior que 30000
		//apenas ilustrativo para uso de filtros
//		RangeQueryBuilder filterIds = new RangeQueryBuilder("id").gt("30000");
//		boolQueryBuilder.filter(filterIds);

		//timeout no servidor de 5 segundos
		searchSourceBuilder.timeout(
				new TimeValue(5, TimeUnit.SECONDS));

		//ordenar por score em ordem decrescente
		searchSourceBuilder.sort(
				new ScoreSortBuilder().order(SortOrder.DESC));
		//havendo empate, retornar o documento de id menor
		searchSourceBuilder.sort(
				new FieldSortBuilder("_id").order(SortOrder.ASC));

		//campos a serem incluidos ou excluidos no retorno do Elasticsearch
		String[] includeFields = new String[]{"url", "title"};
		String[] excludeFields = new String[]{};
		searchSourceBuilder.fetchSource(includeFields, excludeFields);

		//realizando a busca no indice
		SearchRequest searchRequest = new SearchRequest(idxName);

		//recuperando os resultados e imprimindo na tela
		searchRequest.source(searchSourceBuilder);
		StringBuilder result = new StringBuilder();

		JSONObject json = new JSONObject();

		JSONArray list = new JSONArray();

		try {
			SearchResponse sr = client.search(searchRequest, RequestOptions.DEFAULT);
			SearchHits sh = sr.getHits();

			for(SearchHit hit: sh){
				Map<String, Object> source = hit.getSourceAsMap();
				String url = (String) source.get("url");
				String title = (String) source.get("title");
				String abst = hit.getHighlightFields().get("content").getFragments()[0].toString();

				json.put("url", url);
				json.put("title", title);
				json.put("content", abst);
				list.put(json);

			}

		} catch (IOException ex) {
			Logger.getLogger(ELManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return list;
	}
}
