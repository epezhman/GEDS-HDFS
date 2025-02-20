//
// Copyright 2022- IBM Inc. All rights reserved
// SPDX-License-Identifier: Apache-2.0
//

package com.ibm.geds.hdfs;

import com.ibm.geds.GEDS;
import com.ibm.geds.GEDSConfig;

import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

public class GEDSInstance {
    private static GEDS instance;
    private static GEDSConfig instanceConfig;

    public synchronized static GEDS initialize(Configuration conf) {
        if (instance == null) {
            GEDSConfig gedsConfig = getConfig(conf);
            instance = new GEDS(gedsConfig);
        }
        return instance;
    }

    public synchronized static GEDSConfig getConfig(Configuration conf) {
        if (instanceConfig == null) {
            String metadataServer = conf.get(Constants.GEDS_PREFIX + Constants.METADATA_SERVER, "localhost:4381");
            instanceConfig = new GEDSConfig(metadataServer);

            // Use defaults from GEDS.
            String local_storage_path = conf.get(Constants.GEDS_PREFIX + Constants.LOCAL_STORAGE_PATH, "");
            if (local_storage_path != "") {
                instanceConfig.set(Constants.LOCAL_STORAGE_PATH, local_storage_path);
            }
            int port = conf.getInt(Constants.GEDS_PREFIX + Constants.PORT, 0);
            if (port != 0) {
                instanceConfig.set(Constants.PORT, port);
            }

            long cache_block_size = conf.getLong(Constants.GEDS_PREFIX + Constants.CACHE_BLOCK_SIZE, 0);
            if (cache_block_size != 0) {
                instanceConfig.set(Constants.CACHE_BLOCK_SIZE, cache_block_size);
            }

            int http_server_port = conf.getInt(Constants.GEDS_PREFIX + Constants.HTTP_SERVER_PORT, 0);
            if (http_server_port != 0) {
                instanceConfig.set(Constants.HTTP_SERVER_PORT, http_server_port);
            }
            long available_local_memory = conf.getLong(Constants.GEDS_PREFIX + Constants.AVAILABLE_LOCAL_MEMORY, 0);
            if (available_local_memory != 0) {
                instanceConfig.set(Constants.AVAILABLE_LOCAL_MEMORY, available_local_memory);
            }
            long available_local_storage = conf.getLong(Constants.GEDS_PREFIX + Constants.AVAILABLE_LOCAL_STORAGE, 0);
            if (available_local_storage != 0) {
                instanceConfig.set(Constants.AVAILABLE_LOCAL_STORAGE, available_local_storage);
            }
            long io_thread_pool_size = conf.getLong(Constants.GEDS_PREFIX + Constants.IO_THREAD_POOL_SIZE, 0);
            if (io_thread_pool_size != 0) {
                instanceConfig.set(Constants.IO_THREAD_POOL_SIZE, io_thread_pool_size);
            }
            if (conf.get(Constants.GEDS_PREFIX + Constants.PUB_SUB_ENABLED, "false").equalsIgnoreCase("true")) {
                instanceConfig.set(Constants.PUB_SUB_ENABLED, "true");
            }

        }
        return instanceConfig;
    }

    public synchronized static GEDS initialize(String bucket, Configuration conf) throws IOException {
        GEDS geds = initialize(conf);

        String bucketAccessKey = conf.get(Constants.GEDS_PREFIX + bucket + ".accessKey");
        String bucketSecretKey = conf.get(Constants.GEDS_PREFIX + bucket + ".secretKey");
        String bucketEndpoint = conf.get(Constants.GEDS_PREFIX + bucket + ".endpoint");

        boolean hasAccessKey = bucketAccessKey != null;
        boolean hasSecretKey = bucketSecretKey != null;
        boolean hasBucketEndpoint = bucketEndpoint != null;
        if (hasAccessKey && hasSecretKey && hasBucketEndpoint) {
            geds.registerObjectStoreConfig(bucket, bucketEndpoint, bucketAccessKey, bucketSecretKey);
        } else if (hasAccessKey || hasSecretKey || hasBucketEndpoint) {
            throw new RuntimeException("Bucket " + bucket
                    + " has either an accessKey, secretKey or an endpoint registered. To map the bucket to S3 all variables need to be configured.");
        }

        return geds;
    }
}
