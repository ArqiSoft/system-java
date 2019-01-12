# Leanda.io JAVA Tools

## Prerequisites

* [Docker](https://www.docker.com/get-started)
* [GnuPG](https://www.gnupg.org/download/index.html)

## Make sure you have your key GnuPG generated and the public key is uploaded

Follow [Documentation](https://www.gnupg.org/gph/en/manual.html#AEN26)

```terminal
gpg -K                             # list your keys
gpg --send-key YOUR_PUBLIC_KEY_ID  # publish key
```

## Build

```terminal
mvn clean
```

## Publish to maven.org

```terminal
mvn -DskipTests clean deploy
```

## Troubleshooting

If you are seeing this error while publishing:

`gpg: signing failed: Inappropriate ioctl for device`

Please run

```terminal
export GPG_TTY=$(tty)
```
