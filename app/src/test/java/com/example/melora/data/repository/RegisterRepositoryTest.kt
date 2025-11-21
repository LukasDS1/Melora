package com.example.melora.data.repository

import com.example.melora.data.remote.RegisterApi
import com.example.melora.data.remote.dto.ArtistProfileData
import com.example.melora.data.remote.dto.RegisterUserDto
import com.example.melora.data.remote.dto.RolDto
import com.example.melora.data.remote.dto.SongDetailedDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals

class RegisterRepositoryTest {

    //TODO:CAMBIAR STATUS CODE


    // Prueba que indica que el usuario la api devuelve el usuario correctamente
    @Test
    fun getUserRegister_returns_api_ok() = runBlocking {
        val api = mockk<RegisterApi>()

        val repo = RegisterApiRepository(api)

        val rolDto = RolDto(1)

        val sample = RegisterUserDto("test@gmail.com","TestUser12#", rolDto,"Test",null)

        val responseOk = Response.success(sample)

        coEvery { api.getUserById(1) } returns responseOk

        val result = repo.getById(1)

        assertTrue(result.isSuccess)

        val body = result.getOrNull() as RegisterUserDto
        assertEquals("test@gmail.com",body.email)
        assertEquals("TestUser12#",body.password)
        assertEquals("Test",body.nickname)
        assertEquals(1,body.rol.idRol)

    }
    // Prueba que indica que el usaurio en la api no ha sido encontrado
    @Test
    fun getUserRegister_returns_api_notFound() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val response404 = Response.error<RegisterUserDto>(
            404,"User Not Found".toResponseBody()
        )

        coEvery {  api.getUserById(1) } returns response404

        val result = repo.getById(1)

        assertTrue(result.isFailure)
    }

    // Prueba que indica que el usaurio en la api ha sido borrado
    @Test
    fun deleteUser_returns_success() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val response = Response.success(Any())

        coEvery { api.deleteUser(1) } returns response

        val result = repo.deleteUser(1)

        assertTrue(result.isSuccess)

        assertEquals("Usuario eliminado exitosamente",result.getOrNull())

    }

    //Prueba que indica que el usuario en la api no ha podido ser borrado

    @Test
    fun registerDeleteUser_returns_isFailure() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val response = Response.error<Any>(
            404,"No se pudo eliminar el usuario".toResponseBody()
        )

        coEvery { api.deleteUser(1) } returns response

        val result = repo.deleteUser(1)

        assertTrue(result.isFailure)

        val errorMsg = result.exceptionOrNull()?.message
        assertEquals("No se pudo eliminar el usuario",errorMsg)

    }

    // Prueba que indica que el usuario ha sido registrado correctamente
    @Test
    fun registerUser_returns_ok() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val rolDto = RolDto(1)
        val sample = RegisterUserDto("test@gmail.com","TestUser12#", rolDto,"Test",null)

        val responseOK = Response.success(Any())

        coEvery { api.registerUser(sample) } returns responseOK

        val result = repo.register(sample)

        assertTrue(result.isSuccess)
        assertEquals("Usuario registrado correctamente",result.getOrNull())
    }

    // Prueba que indica que el usuario no ha podido ser registrado
    @Test
    fun registerUser_returns_fail() = runBlocking {
        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val rolDto = RolDto(1)
        val sample = RegisterUserDto("test@gmail.com","TestUser12#", rolDto,"Test",null)

        val response = Response.error<Any>(
            404,"Error desconocido".toResponseBody()
        )

        coEvery { api.registerUser(sample) } returns response

        val result = repo.register(sample)

        assertTrue(result.isFailure)

        val msg = result.exceptionOrNull()?.message

        assertEquals("Error desconocido", msg)
    }
    // Prueba de que el usuario actualizado devuelve succes
    @Test
    fun registerUpdateUser_returns_OK() = runBlocking {
        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val rolDto = RolDto(1)
        val sample = RegisterUserDto("test@gmail.com","TestUser12#", rolDto,"Test",null)
        val id = 1L

        val response = Response.success(Any())

        coEvery { api.updateUser(id,sample) } returns response

        val result = repo.updateUser(id,sample)

        assertTrue(result.isSuccess)
        assertEquals("Usuario actualizado exitosamente",result.getOrNull())
    }

    // Prueba de que el usuario actualizado devuelve error
    @Test
    fun registerUpdateUser_returns_failure() = runBlocking {
        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val rolDto = RolDto(1)
        val sample = RegisterUserDto("test@gmail.com","TestUser12#", rolDto,"Test",null)
        val id = 1L

        val responseError = Response.error<Any>(
            400,
            "Error al actualizar usuario".toResponseBody()
        )

        coEvery { api.updateUser(id, sample) } returns responseError

        val result = repo.updateUser(id, sample)

        assertTrue(result.isFailure)
        assertEquals("Error al actualizar usuario", result.exceptionOrNull()?.message)
    }

    //Prueba de que el encuentra el usuario
    @Test
    fun registerSearchUserByNick_retunrs_succes() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val Songs = listOf<SongDetailedDto>(
            SongDetailedDto(1,"Test",null, 2000,
                null, null,100L,1,"Test",null,null))

        val sample = listOf<ArtistProfileData>(ArtistProfileData(1,"Test","Test@gmail.com",null,1L,Songs))

        val query = "Test"

        coEvery { api.searchUsers(query) } returns sample

        val result = repo.searchByNickname(query)

        assertTrue(result.isSuccess)
        assertEquals(sample, result.getOrNull())

    }

    //Prueba que no encuentra el usuario
    @Test
    fun registerSearchUserByNick_returns_failure() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val query = "Test"

        coEvery { api.searchUsers(query) } throws Exception("Error al buscar")

        val result = repo.searchByNickname(query)

        assertTrue(result.isFailure)
        assertEquals("Error al buscar", result.exceptionOrNull()?.message)
    }

    //Prueba que obtiene el mapeo del usuario
    @Test
    fun getRawUserById_returns_success() = runBlocking {

        val api = mockk<RegisterApi>()
        val repo = RegisterApiRepository(api)

        val id = 1L

        val sampleMap = mapOf(
            "email" to "test@gmail.com",
            "nickname" to "Test",
            "rol" to 1L
        )

        coEvery { api.getRawUserById(id) } returns sampleMap

        val result = repo.getUserAsMap(id)

        assertEquals(sampleMap, result)
        assertEquals("test@gmail.com", result["email"])
        assertEquals("Test", result["nickname"])
        assertEquals(1L, result["rol"])
    }
}