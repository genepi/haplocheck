## Release Procedure ##

### Checkout master ###

Update your repository and run all tests with 'mvn test'. If an error is raised, it needs to be fixed and committed before going to the next step.

### Update version number

Update version according Semantic Versioning:

> Given a version number MAJOR.MINOR.PATCH, increment the:
>
> - MAJOR version when you make incompatible API changes,
> - MINOR version when you add functionality in a backwards-compatible manner, and
> - PATCH version when you make backwards-compatible bug fixes.
>
> Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.
>
> https://semver.org/


We track our current version in
- `pom.xml`
- `files/cloudgene.yaml`
- `files/haplocheck.html`
- `Version in README.md`

Set the new version without the `v` prefix.

Then, commit and push the changes:

    git commit -m 'Prepare release 1.x.x'

**This should be the last commit before the release.**


### Create the release on GitHub
