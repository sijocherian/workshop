package dev.cjo.starter.ms.syndtarget.dao.model;

/**
 * @author sijocherian
 */
public interface IndexSchema {
    String indexName = "idx_syndication";
    public enum SyndicationType {
        FACEBOOK,
        TWITTER;

        @Override
        public String toString() {
            return super.toString();
        }
    }; //FTP

    public interface SyndTarget {
        String type = "syndtarget";
    }

    public interface AssetSynd {
        String type = "assetsynd";
    }
}
