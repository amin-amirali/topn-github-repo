# topn-github-repo

This library takes a github repo and counts its top 10 occurring words, outputting them
from most famous downwards.

It uses GitHub's API to obtain the data: https://developer.github.com/v3/issues/

In order to avoid counting similar words, a stemming library is used:
https://github.com/dkmarley/peco

## Usage

To package into a jar, run: `lein uberjar`

Tests can be executed by running: `lein test`

Once the jar is created, you can run it by executing: `java -jar topn-github-repo.jar`

## Limitations

The GitHub API limits the amount of requests for all unauthenticated calls:
https://developer.github.com/v3/#rate-limiting

Since this application is running quite some calls, it will easily exceed the limit if executed more than
once within one hour. To get around this without having to wait, we can use simple authentication.

This feature has not been included since it would require the user to provide a password - which is
private information and could hinder the usage of this app.

## Improvements

The number of API calls is hardcoded: no matter if the repo has less than 1000 issues, the app
will still execute a fixed amount of calls to obtain a total of 1000 issues. This could be solved
by exploring the API further. A possible solution would be:
 - Get a list of all issue links
 - Truncate the list by 1000
 - Curl through the list 1-by-1 to get the required data
 - Proceed with data treating (stemming and counting)
 
 This would mean, though, that we would run 1001 API calls, and that exceeds the limitation of 60/hour
 that is given for unauthenticated calls.
 
## Issues

Since the app currently doesn't have the ability to obtain and loop through a list of issues, it can
happen that an issue gets lost / repeated, if the list of issues gets updated during the execution
of this app.