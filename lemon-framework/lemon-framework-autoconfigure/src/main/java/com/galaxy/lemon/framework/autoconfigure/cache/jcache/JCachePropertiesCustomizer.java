package com.galaxy.lemon.framework.autoconfigure.cache.jcache;

import com.galaxy.lemon.framework.autoconfigure.cache.CacheProperties;

import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import java.util.Properties;
/**
 *
 * Callback interface that can be implemented by beans wishing to customize the properties
 * used by the {@link CachingProvider} to create the {@link CacheManager}.
 * @author yuzhou
 * @date 2018/2/2
 * @time 19:59
 * @since
 */
interface JCachePropertiesCustomizer {

	/**
	 * Customize the properties.
	 * @param cacheProperties the cache properties
	 * @param properties the current properties
	 * @see CachingProvider#getCacheManager(java.net.URI, ClassLoader, Properties)
	 */
	void customize(CacheProperties cacheProperties, Properties properties);

}
