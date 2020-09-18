# [Kubernetes on AWS～애플리케이션 엔지니어 서비스 환경 준비] 소스 코드 배포용 저장소
[Kubernetes on AWS～애플리케이션 엔지니어 서비스 환경 준비] (ric telecom　2020년)에서 사용하고 있는 파일 배포용 저장소입니다.

## db-docker-compose 폴더에 대하여

db-docker-compose 폴더는, 개발 환경에서 애플리케이션을 테스트하기 위해 사용
docker-compose용 설정 파일과 DB 사용자, 데이터베이스 생성용 스크립트를 저장하고 있습니다.

위의 내용은 이 책에서 사용하지 않지만 개발 환경에서 애플리케이션을 동작 시킬 때 사용해 주십시요.

사용 방법은 아래와 같습니다. db-docker-comopse 폴더에 복사하여 실행해 주십시요.

### DB 기동

```
$ docker-compose up -d
```

### DB 정지

```
$ docker-compose down
```

### DB 사용자 생성

```
$ ./createuser.sh
```

### DB 생성

```
$ ./createdb.sh
```
