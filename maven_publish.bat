./gradlew clean
./gradlew :auto-test:signReleasePublication
./gradlew :auto-test:publishReleasePublicationToOSSRHRepository

TOKEN=$(printf '%s:%s' "$MAVEN_USERNAME" "$MAVEN_PASSWORD" | base64 | tr -d '\r\n')

curl -X POST \
  -H "Authorization: Bearer $TOKEN" \
  "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/io.github.lyh20001111"