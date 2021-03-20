package de.essquare.driftbottle.infrastructure;

import java.util.Objects;

import software.amazon.awscdk.services.s3.Bucket;

public class FrontendConfigCreatorProps {

    private Bucket bucket;
    private String userpoolId;
    private String userpoolClientId;
    private String region;

    public FrontendConfigCreatorProps() {
    }

    public FrontendConfigCreatorProps(Bucket bucket, String userpoolId, String userpoolClientId, String region) {
        this.bucket = bucket;
        this.userpoolId = userpoolId;
        this.userpoolClientId = userpoolClientId;
        this.region = region;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(final Bucket bucket) {
        this.bucket = bucket;
    }

    public String getUserpoolId() {
        return userpoolId;
    }

    public void setUserpoolId(final String userpoolId) {
        this.userpoolId = userpoolId;
    }

    public String getUserpoolClientId() {
        return userpoolClientId;
    }

    public void setUserpoolClientId(final String userpoolClientId) {
        this.userpoolClientId = userpoolClientId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FrontendConfigCreatorProps that = (FrontendConfigCreatorProps) o;
        return Objects.equals(bucket, that.bucket)
               && Objects.equals(userpoolId, that.userpoolId)
               && Objects.equals(userpoolClientId, that.userpoolClientId)
               && Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucket, userpoolId, userpoolClientId, region);
    }

    @Override
    public String toString() {
        return "FrontendConfigCreatorProps{" +
               "bucket=" + bucket +
               ", userpoolId='" + userpoolId + '\'' +
               ", userpoolClientId='" + userpoolClientId + '\'' +
               ", region='" + region + '\'' +
               '}';
    }

    public static FrontendConfigCreatorPropsBuilder builder() {
        return FrontendConfigCreatorPropsBuilder.builder();
    }

    public static final class FrontendConfigCreatorPropsBuilder {
        private Bucket bucket;
        private String userpoolId;
        private String userpoolClientId;
        private String region;

        private FrontendConfigCreatorPropsBuilder() {}

        public static FrontendConfigCreatorPropsBuilder builder() {
            return new FrontendConfigCreatorPropsBuilder();
        }

        public FrontendConfigCreatorPropsBuilder bucket(Bucket bucket) {
            this.bucket = bucket;
            return this;
        }

        public FrontendConfigCreatorPropsBuilder userpoolId(String userpoolId) {
            this.userpoolId = userpoolId;
            return this;
        }

        public FrontendConfigCreatorPropsBuilder userpoolClientId(String userpoolClientId) {
            this.userpoolClientId = userpoolClientId;
            return this;
        }

        public FrontendConfigCreatorPropsBuilder region(String region) {
            this.region = region;
            return this;
        }

        public FrontendConfigCreatorProps build() {
            FrontendConfigCreatorProps frontendConfigCreatorProps = new FrontendConfigCreatorProps();
            frontendConfigCreatorProps.setBucket(bucket);
            frontendConfigCreatorProps.setUserpoolId(userpoolId);
            frontendConfigCreatorProps.setUserpoolClientId(userpoolClientId);
            frontendConfigCreatorProps.setRegion(region);
            return frontendConfigCreatorProps;
        }
    }
}
