import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AuthRoutingModule } from './auth-routing.module';
import { NbAuthModule, NbAuthService, NbTokenService, NbPasswordAuthStrategy } from '@nebular/auth';
import { 
  NbAlertModule,
  NbButtonModule,
  NbCheckboxModule,
  NbInputModule,

} from '@nebular/theme';
import { LoginComponent } from './login/login.component';

const formSetting: any = {
  redirectDelay: 0,
  showMessages: {
    success: true,
  },
};


@NgModule({
  declarations: [LoginComponent],
  providers: [
    NbAuthService, 
    NbTokenService,
    NbPasswordAuthStrategy,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    NbAlertModule,
    NbInputModule,
    NbButtonModule,
    NbCheckboxModule,
    AuthRoutingModule,
    NbAuthModule.forRoot({
      strategies: [
        NbPasswordAuthStrategy.setup({
          name: 'email',

          baseEndpoint: '/api',
           login: {
             endpoint: '/user/loginwithemail',
            //  method: 'post'
           },
           register: {
             endpoint: '/auth/register',
           },
        }),
      ],
      forms: {
        login: formSetting,
           register: formSetting,
           requestPassword: formSetting,
           resetPassword: formSetting,
           logout: {
             redirectDelay: 0,
           },
      },
    }), 
    
  ]
})
export class AuthModule { }
