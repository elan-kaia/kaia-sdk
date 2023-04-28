from base.testing import KlaytnBaseTesting


class TestShowTally(KlaytnBaseTesting):

    def test_post(self):
        governance_response = self.sdk.governance.show_tally()

        self.covert_response(governance_response.response)
        self.assertResponseSuccess()
        self.assertIn("result", self.response)
