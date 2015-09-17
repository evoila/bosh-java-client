/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bosh.client.v2;

import java.net.URI;

import org.springframework.web.client.RestTemplate;

import io.bosh.client.v2.releases.Releases;
import io.bosh.client.v2.releases.SpringReleases;
import io.bosh.client.v2.stemcells.Stemcells;
import io.bosh.client.v2.stemcells.SpringStemcells;

/**
 * @author David Ehringer
 */
public class SpringDirectorClient implements DirectorClient {
    
    private final RestTemplate restTemplate;
    
    private final Releases releases;
    private final Stemcells stemcells;

    SpringDirectorClient(URI root, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.releases = new SpringReleases(restTemplate, root);
        this.stemcells = new SpringStemcells(restTemplate, root);
    }
    
    public RestTemplate restTemplate(){
        return restTemplate;
    }

    @Override
    public Stemcells stemcells() {
        return stemcells;
    }

    @Override
    public Releases releases() {
        return releases;
    }

}