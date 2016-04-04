package crylio.rest.serializers

import crylio.common.dto.user.{SignInResultDTO, SignInDTO}

trait UserRoutesJson extends JsonProtocol{
  implicit val jsonSignInDTO = jsonFormat2(SignInDTO)
  implicit val jsonSignInResultDTO = jsonFormat1(SignInResultDTO)

}
