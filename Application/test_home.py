import unittest, hello

class TestHome(unittest.TestCase):
    def setUp(self):
        self.app = hello.app.test_client()

    def test_home(self):
        """Test root context."""
        response = self.app.get('/home', follow_redirects=True)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(b'Hello, World!', response.data)

    def test_post(self):
        """Test root context."""
        response = self.app.get('/', follow_redirects=True)
        print(response)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(b'GET method called', response.data)

if __name__ == '__main__':
    unittest.main()