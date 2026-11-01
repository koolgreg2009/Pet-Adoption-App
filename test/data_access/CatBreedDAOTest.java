package data_access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@ExtendWith(MockitoExtension.class)
class CatBreedDAOTest {

	@Mock
	private OkHttpClient mockClient;

	private CatBreedDAO catBreedDAO = new CatBreedDAO();

	@Mock
	private Call mockCall;

	private Response response;

	@Mock
	private ResponseBody mockResponseBody;

	@BeforeEach
	void setUp() {
		// Replace the client in CatBreedDAO with the mocked one
		catBreedDAO = new CatBreedDAO(mockClient);
	}

	@Test
	void testGetBreedInformationSuccess() throws IOException {
		// Given
		String breedName = "American Shorthair";
		String jsonResponse = "[{\"weight\":{\"imperial\":\"8 - 15\",\"metric\":\"4 - 7\"},\"id\":\"asho\",\"name\":\"American Shorthair\",\"cfa_url\":\"http://cfa.org/Breeds/BreedsAB/AmericanShorthair.aspx\",\"vetstreet_url\":\"http://www.vetstreet.com/cats/american-shorthair\",\"vcahospitals_url\":\"https://vcahospitals.com/know-your-pet/cat-breeds/american-shorthair\",\"temperament\":\"Active, Curious, Easy Going, Playful, Calm\",\"origin\":\"United States\",\"country_codes\":\"US\",\"country_code\":\"US\",\"description\":\"The American Shorthair is known for its longevity, robust health, good looks, sweet personality, and amiability with children, dogs, and other pets.\",\"life_span\":\"15 - 17\",\"indoor\":0,\"lap\":1,\"alt_names\":\"Domestic Shorthair\",\"adaptability\":5,\"affection_level\":5,\"child_friendly\":4,\"dog_friendly\":5,\"energy_level\":3,\"grooming\":1,\"health_issues\":3,\"intelligence\":3,\"shedding_level\":3,\"social_needs\":4,\"stranger_friendly\":3,\"vocalisation\":3,\"experimental\":0,\"hairless\":0,\"natural\":1,\"rare\":0,\"rex\":0,\"suppressed_tail\":0,\"short_legs\":0,\"wikipedia_url\":\"https://en.wikipedia.org/wiki/American_Shorthair\",\"hypoallergenic\":0,\"reference_image_id\":\"JFPROfGtQ\",\"image\":{\"id\":\"JFPROfGtQ\",\"width\":1600,\"height\":1200,\"url\":\"https://cdn2.thecatapi.com/images/JFPROfGtQ.jpg\"}}]";
		HashMap<String, Object> expectedMap = new HashMap<>();
		expectedMap.put("name", "American Shorthair");
		expectedMap.put("description",
				"The American Shorthair is known for its longevity, robust health, good looks, sweet personality, and amiability with children, dogs, and other pets.");
		expectedMap.put("adaptability", 5);
		expectedMap.put("affection_level", 5);
		expectedMap.put("child_friendly", 4);
		expectedMap.put("dog_friendly", 5);
		expectedMap.put("energy_level", 3);
		expectedMap.put("image_url", "https://cdn2.thecatapi.com/images/JFPROfGtQ.jpg");

		response = new Response.Builder()
				.request(new Request.Builder().url("https://cdn2.thecatapi.com")
						.post(new FormBody.Builder().add("message", "test message").build()).build())
				.code(200).protocol(Protocol.HTTP_2).message("success").header("MOCKED_HEADER_KEY", "test")
				.body(ResponseBody.create(MediaType.parse("image/*"), jsonResponse)).build();

		when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
		when(mockCall.execute()).thenReturn(response);

		// When
		HashMap<String, Object> result = catBreedDAO.getBreedInformation(breedName);

		// Then
		assertEquals(expectedMap, result);
		verify(mockClient).newCall(any(Request.class));
		verify(mockCall).execute();
	}

	@Test
	void testGetBreedInformationFailure() throws IOException {
		// Given
		String breedName = "NonExistentBreed";
		response = new Response.Builder()
				.request(new Request.Builder().url("https://cdn2.thecatapi.com")
						.post(new FormBody.Builder().add("message", "test message").build()).build())
				.code(500).protocol(Protocol.HTTP_2).message("success").header("MOCKED_HEADER_KEY", "test")
				.body(ResponseBody.create(MediaType.parse("image/*"), "")).build();

		when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
		when(mockCall.execute()).thenReturn(response);
		// When
		HashMap<String, Object> result = catBreedDAO.getBreedInformation(breedName);

		// Then
		assertEquals(null, result);
		verify(mockClient).newCall(any(Request.class));
		verify(mockCall).execute();
	}

	@Test
	void testGetBreedInformationException() throws IOException {
		// Given
		String breedName = "AnotherBreed";
		when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
		when(mockCall.execute()).thenThrow(new IOException("Network error"));

		// When
		HashMap<String, Object> result = catBreedDAO.getBreedInformation(breedName);

		// Then
		assertEquals(null, result);
		verify(mockClient).newCall(any(Request.class));
		verify(mockCall).execute();
	}

	@Test
	void testParseJsonResponseEmpty() throws IOException {
		String breedName = "American Shorthair";
		String jsonResponse = "[]";

		response = new Response.Builder()
				.request(new Request.Builder().url("https://cdn2.thecatapi.com")
						.post(new FormBody.Builder().add("message", "test message").build()).build())
				.code(200).protocol(Protocol.HTTP_2).message("success").header("MOCKED_HEADER_KEY", "test")
				.body(ResponseBody.create(MediaType.parse("image/*"), jsonResponse)).build();

		when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
		when(mockCall.execute()).thenReturn(response);

		// When
		HashMap<String, Object> result = catBreedDAO.getBreedInformation(breedName);

		// Then
		assertEquals(null, result);
		verify(mockClient).newCall(any(Request.class));
		verify(mockCall).execute();
	}

    private void reply(String json) throws IOException {
        Response response = new Response.Builder().request(new Request.Builder().url("https://example.org").build())
                .code(200).protocol(Protocol.HTTP_1_1).message("OK")
                .body(ResponseBody.create(MediaType.parse("application/json"), json)).build();
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);
    }

    @Test void missingOptionalImageDoesNotDiscardBreed() throws IOException {
        reply("[{\"name\":\"Siamese\",\"description\":\"Friendly\",\"adaptability\":5,"
                + "\"affection_level\":5,\"child_friendly\":4,\"dog_friendly\":4,\"energy_level\":5}]");
        assertEquals("", catBreedDAO.getBreedInformation("Siamese").get("image_url"));
    }

    @Test void malformedResponseReturnsFailure() throws IOException {
        reply("not JSON");
        assertEquals(null, catBreedDAO.getBreedInformation("Siamese"));
    }

    @Test void queryEncodesBreedAsOneParameter() throws IOException {
        reply("[]");
        catBreedDAO.getBreedInformation("A & B");
        org.mockito.ArgumentCaptor<Request> request = org.mockito.ArgumentCaptor.forClass(Request.class);
        verify(mockClient).newCall(request.capture());
        assertEquals("A & B", request.getValue().url().queryParameter("q"));
    }
}
