# topn-github-repo

This library takes a github repo and counts its top 10 occurring words, outputting them
from most famous downwards.

It uses GitHub's API to obtain the data: https://developer.github.com/v3/issues/

A stemming library is being used to avoid counting similar words (for example: "count",
"counter", "counting") as separate:
https://github.com/dkmarley/peco

A few configuration parameters can be found in file: `config/config.edn`, namely:
- Which git owner should be used: `:git-owner`, currently set at `facebook`
- Which repo to from the previous owner do we want to count issues from: `:git-repo`, currently using repo `react` 
- Which field to use to sort issues: `:sort-by`, possible values are created|updated|comments
- Direction in which to sort the issues: `:sort-direction`, possible values are desc|asc
- What state should the issues have: `:state`, possible values are open|closed|all
- How many top words from the issues are to be retreived: `:top-n-words`, currently set at 10
- Which field to use as a source of data: `:data-field`, currently set to :title, possible values are
:title|:body (more can be used but these seem to be the fields from each issue's respose that seem to
make the most sense)

## Usage

To package into a jar, run: `lein test-and-build`

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

It also looks like the GitHub API has been designed for retrieving data for display, rather than
for analytics purposes. This kind of analysis (top-n words in issues) would make a lot of sense to be
done on the backend (where the data could be accessed directly). Alternatively, new end-points could be
created to make the gathering/preparation/analysis of the data easier.
 
## Issues

Since the app currently doesn't have the ability to obtain and loop through a list of issues, it can
happen that an issue gets lost / repeated, if the list of issues gets updated during the execution
of this app.
