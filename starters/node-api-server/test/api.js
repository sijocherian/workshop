//During the test the env variable is set to test
process.env.NODE_ENV = 'test';

//Require the dev-dependencies
let chai = require('chai');
let chaiHttp = require('chai-http');
//const sinon = require('sinon');
let should = chai.should();
(assert = chai.assert), (expect = chai.expect);

let server = require('../app.js');

chai.use(chaiHttp);

//parent block
describe('api-endpoints', () => {
    //Before each test we empty the database
    /*beforeEach((done) => { 
        done();
    });*/

    describe('/GET version', () => {
        it('should return valid version', (done) => {
            chai.request(server)
                .get('/version')
                .end((err, res) => {
                    res.should.have.status(200);
                    res.should.be.json;
                    //res.body.should.be.a('object');
                    res.body.should.have.property('version');
                    expect(res.body.version).to.not.be.empty;
                    //res.body.should.be.a('array');
                    //res.body.length.should.be.eql(0);

                    done();
                });
        });
    });
});
