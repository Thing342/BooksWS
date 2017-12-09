import khttp.delete
import khttp.get
import khttp.post
import khttp.put
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

data class UserRecord(val username: String, var password: String, val userid: Int, var tokenid: Int?)

class UserFacadeRESTTest {
    private val endpoint = "http://localhost:8080/Books-WS-1.0/webresources/"
    private val userList = ArrayList<UserRecord>()

    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        userList.forEach {
            assertEquals(200, login(it))
            val (code, message) = remove(it.tokenid!!)
            assertEquals(200, code)
            assertEquals("Deleted user " + it.username, message)
        }
    }

    @Test
    @Throws(Exception::class)
    fun test_create() {
        // Should properly add new user
        val username = "testuserCreate"
        val password = "testpassword1"
        val email = "create@example.com"

        val (code1, json1) = create(username, email,
                "John", "Doe", password)

        assertEquals(code1, 200)
        assertNotNull(json1)

        assertEquals(username, json1?.getString("username"))
        assertEquals(email, json1?.getString("email"))
        val userid = json1?.getInt("id")
        assertNotNull(userid)

        userList.add(UserRecord(username, password, userid!!, null))

        // Should refuse to create duplicate username
        val (code2, json2) = create(username, "anyemail",
                "John", "Doe", "anypassword")
        assertEquals(code2, 500)
        assertNull(json2)
    }

    @Test
    @Throws(Exception::class)
    fun test_edit() {
        val user = generate_user("editUser", "editPassword")
        assertEquals(200, login(user))

        // should edit normal field
        val (code1, json1) = edit(user.tokenid!!, "new@example.com", "Test", "Test", user.password)
        assertEquals(200, code1)
        val newemail = json1?.optString("email")
        assertEquals("new@example.com", newemail)

        // should edit and re-hash password (and should be able to log in using new password)
        user.password = "anewpassword"
        val (code2, json2) = edit(user.tokenid!!, "new@example.com", "Test", "Test", user.password)
        assertEquals(200, code2)
        assertEquals(200, login(user))

        // should not be able to log in using old password
        user.password = "editPassword"
        assertNotEquals(200, login(user))
        user.password = "anewpassword"

        //should refuse invalid token
        val (code3, _) = edit(-5, "new@example.com", "Test", "Test", user.password)
        assertEquals(401, code3)
    }

    @Test
    @Throws(Exception::class)
    fun test_find() {
        val user = generate_user("findUser", "findPassword")

        // Should find new user
        val (code, json) = find(user.userid)
        assertEquals(200, code)
        assertEquals("findUser", json?.getString("username"))

        // Should 404 on nonexistent user
        assertEquals(404, find(-1).first)
    }

    private fun generate_user(username: String, password: String): UserRecord {
        val (code1, json1) = create(username, "user@example.com", "Test", "Test", password)
        assertEquals(200, code1)

        val uid = json1?.optInt("id")
        assertNotNull(uid)
        assertNotEquals(0, uid)
        val user = UserRecord(username, password, uid!!, null)
        userList.add(user)

        return user
    }

    private fun create(username: String, email: String, firstname: String, lastname: String, password: String): Pair<Int, JSONObject?> {
        val payload = mapOf(
                "username" to username,
                "email" to email,
                "firstname" to firstname,
                "lastname" to lastname,
                "password" to password
        )
        val resp = post(endpoint + "users/",
                headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
                data = payload)

        return when (resp.statusCode) {
            200 -> (200 to resp.jsonObject)
            else -> (resp.statusCode to null)
        }
    }

    private fun edit(tokenid: Int, email: String, firstname: String, lastname: String, password: String): Pair<Int, JSONObject?> {
        val payload = mapOf(
                "email" to email,
                "firstname" to firstname,
                "lastname" to lastname,
                "password" to password
        )
        val resp = put(endpoint + "users/",
                headers = mapOf("Content-Type" to "application/x-www-form-urlencoded", "tokenid" to tokenid.toString()),
                data = payload)

        return when (resp.statusCode) {
            200 -> (200 to resp.jsonObject)
            else -> (resp.statusCode to null)
        }
    }

    private fun remove(tokenid: Int): Pair<Int, String?> {
        val resp = delete(endpoint + "users/",
                headers = mapOf("tokenid" to tokenid.toString()))

        return when (resp.statusCode) {
            200 -> (200 to resp.text)
            else -> (resp.statusCode to null)
        }
    }

    private fun find (userid: Int): Pair<Int, JSONObject?> {
        val resp = get(endpoint + "users/$userid")
        return when (resp.statusCode) {
            200 -> (200 to resp.jsonObject)
            else -> (resp.statusCode to null)
        }
    }

    private fun login (user: UserRecord): Int {
        val resp = post(endpoint + "login",
                headers = mapOf(
                        "username" to user.username,
                        "password" to user.password
                ))

        return if (resp.statusCode == 200) {
            val tokenid = resp.jsonObject.getInt("id")
            if (tokenid != 0) {
                user.tokenid = tokenid
                200
            } else 400
        } else resp.statusCode
    }

    private fun logout (user: UserRecord): Int {
        if (user.tokenid == null) return 400

        val resp = delete(endpoint + "login", headers = mapOf("id" to user.userid.toString()))
        if(resp.statusCode == 200) user.tokenid = null

        return resp.statusCode
    }
}