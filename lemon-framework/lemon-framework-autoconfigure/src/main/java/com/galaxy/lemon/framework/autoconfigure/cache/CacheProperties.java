/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.galaxy.lemon.framework.autoconfigure.cache;

import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@ConfigurationProperties(prefix = "lemon.cache")
public class CacheProperties {

	/**
	 * Cache type, auto-detected according to the environment by default.
	 */
	private CacheType type;

	/**
	 * Comma-separated list of cache names to create if supported by the underlying cache
	 * manager. Usually, this disables the ability to create additional caches on-the-fly.
	 */
	private List<String> cacheNames = new ArrayList<String>();

	private final Caffeine caffeine = new Caffeine();

	private final Couchbase couchbase = new Couchbase();

	private final EhCache ehcache = new EhCache();

	private final Hazelcast hazelcast = new Hazelcast();

	private final Infinispan infinispan = new Infinispan();

	private final JCache jcache = new JCache();

	private final Guava guava = new Guava();

	public CacheType getType() {
		return this.type;
	}

	public void setType(CacheType mode) {
		this.type = mode;
	}

	public List<String> getCacheNames() {
		return this.cacheNames;
	}

	public void setCacheNames(List<String> cacheNames) {
		this.cacheNames = cacheNames;
	}

	public Caffeine getCaffeine() {
		return this.caffeine;
	}

	public Couchbase getCouchbase() {
		return this.couchbase;
	}

	public EhCache getEhcache() {
		return this.ehcache;
	}

	@Deprecated
	public Hazelcast getHazelcast() {
		return this.hazelcast;
	}

	public Infinispan getInfinispan() {
		return this.infinispan;
	}

	public JCache getJcache() {
		return this.jcache;
	}

	public Guava getGuava() {
		return this.guava;
	}

	/**
	 * Resolve the config location if set.
	 * @param config the config resource
	 * @return the location or {@code null} if it is not set
	 * @throws IllegalArgumentException if the config attribute is set to an unknown
	 * location
	 */
	public Resource resolveConfigLocation(Resource config) {
		if (config != null) {
			Assert.isTrue(config.exists(), "Cache configuration does not exist '"
					+ config.getDescription() + "'");
			return config;
		}
		return null;
	}

	/**
	 * Caffeine specific cache properties.
	 */
	public static class Caffeine {

		/**
		 * The spec to use to create caches. Check CaffeineSpec for more details on the
		 * spec format.
		 */
		private String spec;

		public String getSpec() {
			return this.spec;
		}

		public void setSpec(String spec) {
			this.spec = spec;
		}

	}

	/**
	 * Couchbase specific cache properties.
	 */
	public static class Couchbase {

		/**
		 * Entry expiration in milliseconds. By default the entries never expire. Note
		 * that this value is ultimately converted to seconds.
		 */
		private int expiration;

		public int getExpiration() {
			return this.expiration;
		}

		/**
		 * Return the expiration in seconds.
		 * @return the expiration in seconds
		 */
		public int getExpirationSeconds() {
			return (int) TimeUnit.MILLISECONDS.toSeconds(this.expiration);
		}

		public void setExpiration(int expiration) {
			this.expiration = expiration;
		}

	}

	/**
	 * EhCache specific cache properties.
	 */
	public static class EhCache {

		/**
		 * The location of the configuration file to use to initialize EhCache.
		 */
		private Resource config;

		public Resource getConfig() {
			return this.config;
		}

		public void setConfig(Resource config) {
			this.config = config;
		}

	}

	/**
	 * Hazelcast specific cache properties.
	 */
	@Deprecated
	public static class Hazelcast {

		/**
		 * The location of the configuration file to use to initialize Hazelcast.
		 */
		private Resource config;

		@DeprecatedConfigurationProperty(replacement = "spring.hazelcast.config", reason = "Use general hazelcast auto-configuration instead.")
		@Deprecated
		public Resource getConfig() {
			return this.config;
		}

		public void setConfig(Resource config) {
			this.config = config;
		}

	}

	/**
	 * Infinispan specific cache properties.
	 */
	public static class Infinispan {

		/**
		 * The location of the configuration file to use to initialize Infinispan.
		 */
		private Resource config;

		public Resource getConfig() {
			return this.config;
		}

		public void setConfig(Resource config) {
			this.config = config;
		}

	}

	/**
	 * JCache (JSR-107) specific cache properties.
	 */
	public static class JCache {

		/**
		 * The location of the configuration file to use to initialize the cache manager.
		 * The configuration file is dependent of the underlying cache implementation.
		 */
		private Resource config;

		/**
		 * Fully qualified name of the CachingProvider implementation to use to retrieve
		 * the JSR-107 compliant cache manager. Only needed if more than one JSR-107
		 * implementation is available on the classpath.
		 */
		private String provider;

		public String getProvider() {
			return this.provider;
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public Resource getConfig() {
			return this.config;
		}

		public void setConfig(Resource config) {
			this.config = config;
		}

	}

	/**
	 * Guava specific cache properties.
	 */
	public static class Guava {

		/**
		 * The spec to use to create caches. Check CacheBuilderSpec for more details on
		 * the spec format.
		 */
		private String spec;

		@Deprecated
		@DeprecatedConfigurationProperty(reason = "Caffeine will supersede the Guava support in Spring Boot 2.0", replacement = "spring.cache.caffeine.spec")
		public String getSpec() {
			return this.spec;
		}

		@Deprecated
		public void setSpec(String spec) {
			this.spec = spec;
		}

	}

}
