# dhis2-android-dataentry
An android client which enables users to capture routine, event and tracker data.

Debug builds work out of the box, however for release builds a fabric key is required.


For release builds, a fabric key has to be provided to the build process. In order to obtain a fabric key, one has to register for an account at https://fabric.io/.
The key can be found in fabric under settings/organisations/yourOrganisation > then by clicking on the API eky.

The fabric key should then be put into dhis2-android-dataentry/app/fabric.properties in the build directory:
apiKey=<your fabric key>

(without <> or quotes)
Meaning that for forks or clone release builds the builder shsould register for a fabric account and provide that
fabric key.