import unittest, hello

class TestExit(unittest.TestCase):
    def setUp(self):
        self.app = hello.app.test_client()

    def test_exit(self):
        """Test root context."""
        response = self.app.get('/exit', follow_redirects=True)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(b'Goodbye!', response.data)

if __name__ == '__main__':
    unittest.main()